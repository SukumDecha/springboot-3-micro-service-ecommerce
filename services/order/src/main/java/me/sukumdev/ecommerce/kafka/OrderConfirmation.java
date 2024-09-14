package me.sukumdev.ecommerce.kafka;

import me.sukumdev.ecommerce.customer.CustomerResponse;
import me.sukumdev.ecommerce.order.PaymentMethod;
import me.sukumdev.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation
        (
                String orderReference,
                BigDecimal totalAmount,
                PaymentMethod paymentMethod,
                CustomerResponse customer,
                List<PurchaseResponse> products
        ) {
}
