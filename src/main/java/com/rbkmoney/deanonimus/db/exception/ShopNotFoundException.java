package com.rbkmoney.deanonimus.db.exception;

public class ShopNotFoundException extends RuntimeException {

    public ShopNotFoundException(String id) {
        super("Not found with id: " + id);
    }
}
