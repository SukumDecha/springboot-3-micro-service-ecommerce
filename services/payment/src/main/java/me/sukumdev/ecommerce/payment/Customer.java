package me.sukumdev.ecommerce.payment;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

//Use @Validated to validate this object when it's on the nested object
//of the PaymentRequest object
@Validated
public record Customer(
        String id,
        @NotNull(message =  "First name is required")
        String firstname,
        @NotNull(message =  "Last name is required")
        String lastname,
        @NotNull(message =  "Email is required")
        @Email(message = "Email is invalid")
        String email
) {
}
