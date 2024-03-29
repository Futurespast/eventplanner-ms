package com.eventplanner.participants.utils;

public class InvalidEmailAddressException extends RuntimeException{
    public InvalidEmailAddressException(String emailAddress) {
        super("Invalid email address provided: " + emailAddress);
    }
}
