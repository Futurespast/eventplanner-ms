package com.eventplanner.events.dataacesslayer;


import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "events")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    private String id;
    @Indexed(unique = true)
    private EventIdentifier eventIdentifier;
    private CustomerModel customerModel;
    private VenueModel venueModel;
    private List<ParticipantModel> participantModels;
    private EventDate eventDate;
    private String eventName;
    private String description;
    private EventStatus eventStatus;
}
