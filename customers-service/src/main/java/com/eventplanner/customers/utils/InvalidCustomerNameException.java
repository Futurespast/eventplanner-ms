package com.eventplanner.customers.utils;

public class InvalidCustomerNameException extends RuntimeException {

    public InvalidCustomerNameException(String name) {
        super("Invalid customer name provided: " + name);
    }


}
