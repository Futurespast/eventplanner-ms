package com.eventplanner.participants.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantRequestModel {
   // private String eventId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String specialNote;
}
