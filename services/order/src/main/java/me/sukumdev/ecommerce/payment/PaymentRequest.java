package me.sukumdev.ecommerce.payment;

import me.sukumdev.ecommerce.customer.CustomerResponse;
import me.sukumdev.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
