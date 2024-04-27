package com.eventplanner.apigateway.presentationlayer.participants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
//import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponseModel extends RepresentationModel<ParticipantResponseModel> {
    private String participantId;
   // private String eventId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String specialNote;
}
