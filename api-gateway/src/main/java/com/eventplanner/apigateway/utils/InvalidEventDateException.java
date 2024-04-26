package com.eventplanner.apigateway.utils;

public class InvalidEventDateException extends RuntimeException{
    public InvalidEventDateException(String message){super(message);}
}
