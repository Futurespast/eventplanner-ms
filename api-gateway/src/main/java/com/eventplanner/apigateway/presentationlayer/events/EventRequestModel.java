package com.eventplanner.apigateway.presentationlayer.events;


import com.eventplanner.apigateway.domainclientlayer.events.EventDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestModel {
    private String venueId;
    private String customerId;
    private EventDate eventDate;
    private String eventStatus;
    private String eventName;
    private String description;
    private List<String> participantIds;
}
