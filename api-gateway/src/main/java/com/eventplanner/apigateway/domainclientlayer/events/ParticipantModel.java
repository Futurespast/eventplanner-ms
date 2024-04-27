package com.eventplanner.apigateway.domainclientlayer.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class ParticipantModel {
     String participantId;
     String firstName;
     String lastName;
     String emailAddress;
     String specialNote;
}
