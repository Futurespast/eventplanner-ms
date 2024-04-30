package com.eventplanner.events.presentationlayer;

import com.eventplanner.events.dataacesslayer.EventDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
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
