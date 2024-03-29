package com.eventplanner.participants.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponseModel {
    private String participantId;
    //private String eventId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String specialNote;
}
