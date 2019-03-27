package com.app.model.error;

/**
 * Custom exception.
 * - Placeholder to allow future extensions if needed.
 *
 **/
public class ShoppingBasketException extends Exception {

    public ShoppingBasketException(String message, Throwable cause) {
        super(message, cause);
    }
    public ShoppingBasketException(String message) {
        super(message);
    }
}
