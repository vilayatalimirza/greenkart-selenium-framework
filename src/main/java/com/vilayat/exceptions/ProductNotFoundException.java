package com.vilayat.exceptions;

/**
 * Thrown when addItemsToCart() can't find one or more requested products
 * on the page — instead of silently proceeding with fewer items than asked.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}