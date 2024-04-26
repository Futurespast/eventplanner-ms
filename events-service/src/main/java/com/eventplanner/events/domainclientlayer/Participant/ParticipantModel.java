package com.eventplanner.events.domainclientlayer.Participant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
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
