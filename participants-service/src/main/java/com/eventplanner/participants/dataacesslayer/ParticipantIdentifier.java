package com.eventplanner.participants.dataacesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class ParticipantIdentifier {
    private String participantId;

    public ParticipantIdentifier(){this.participantId = UUID.randomUUID().toString();}
    public ParticipantIdentifier(String participantId){this.participantId = participantId;}
}
