package com.eventplanner.venues.utils;

import java.time.LocalDate;

public class PastAvailableDateException extends RuntimeException {

    public PastAvailableDateException(LocalDate date) {
        super("The provided date " + date + " is in the past and cannot be used as an available date.");
    }

}
