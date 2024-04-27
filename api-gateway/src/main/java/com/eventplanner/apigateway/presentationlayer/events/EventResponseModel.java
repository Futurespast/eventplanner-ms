package com.eventplanner.apigateway.presentationlayer.events;


import com.eventplanner.apigateway.domainclientlayer.events.EventDate;


import com.eventplanner.apigateway.domainclientlayer.events.ParticipantModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseModel extends RepresentationModel<EventResponseModel> {
    private String eventId;
    private String venueId;
    private String customerId;
    private List<ParticipantModel>participants;
    private EventDate eventDate;
    private String eventStatus;
    private String eventName;
    private String description;
    private String venueName;
    private String customerFirstName;
    private String customerLastName;
}
