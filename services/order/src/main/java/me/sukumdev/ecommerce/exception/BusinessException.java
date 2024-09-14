package me.sukumdev.ecommerce.exception;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {

    private final String msg;
}
