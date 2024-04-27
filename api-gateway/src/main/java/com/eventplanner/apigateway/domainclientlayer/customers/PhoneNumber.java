package com.eventplanner.apigateway.domainclientlayer.customers;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class PhoneNumber {

    private PhoneType phoneType;
    private String number;

    public PhoneNumber(@NotNull PhoneType phoneType, @NotNull String number) {
        this.phoneType = phoneType;
        this.number = number;
    }
}
