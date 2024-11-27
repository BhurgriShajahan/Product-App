package com.product.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomJwtException extends RuntimeException {

    public CustomJwtException(String message) {
        super(message);
    }

    public CustomJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
