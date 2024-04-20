package com.eventplanner.events.dataacesslayer;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
public class EventDate {
private LocalDate startDate;
private LocalDate endDate;

    public EventDate(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
