package me.sukumdev.ecommerce.order;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sukumdev.ecommerce.customer.CustomerClient;
import me.sukumdev.ecommerce.exception.BusinessException;
import me.sukumdev.ecommerce.kafka.OrderConfirmation;
import me.sukumdev.ecommerce.kafka.OrderProducer;
import me.sukumdev.ecommerce.orderline.OrderLineRequest;
import me.sukumdev.ecommerce.orderline.OrderLineService;
import me.sukumdev.ecommerce.payment.PaymentClient;
import me.sukumdev.ecommerce.payment.PaymentRequest;
import me.sukumdev.ecommerce.product.ProductClient;
import me.sukumdev.ecommerce.product.PurchaseRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;

    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(@Valid OrderRequest request) {
        // check customer -> OpenFei
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with provided ID"));

        // purchase the product --> product-ms (Rest-template)
        var purchasedProducts = this.productClient.purchaseProducts(request.products());
        // persist the order
        var order = repository.save(mapper.toOrder(request));

        // persist the order line
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // todo: start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // send order confirmation  --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map((mapper::fromOrder))
                .orElseThrow(() -> new BusinessException("Order not found with provided ID"));
    }
}
