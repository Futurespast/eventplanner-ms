package com.eventplanner.events.presentationlayer;

import com.eventplanner.events.dataacesslayer.EventDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestModel {
    private String venueId;
    private String customerId;
    private EventDate eventDate;
    private String eventStatus;
    private String name;
    private String description;
}
