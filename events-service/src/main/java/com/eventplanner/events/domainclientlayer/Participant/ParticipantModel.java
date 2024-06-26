package com.eventplanner.events.domainclientlayer.Participant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class ParticipantModel {
     String participantId;
     String firstName;
     String lastName;
     String emailAddress;
     String specialNote;
}
