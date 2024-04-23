package com.eventplanner.events.presentationlayer;


import com.eventplanner.events.dataacesslayer.EventDate;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseModel {
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
