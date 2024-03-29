package com.eventplanner.customers.dataacesslayer;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class PhoneNumber {
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;
    private String number;

    public PhoneNumber(@NotNull PhoneType phoneType, @NotNull String number) {
        this.phoneType = phoneType;
        this.number = number;
    }
}
