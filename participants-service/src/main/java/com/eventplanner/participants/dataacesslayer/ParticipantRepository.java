package com.eventplanner.participants.dataacesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    Participant findParticipantByParticipantIdentifier_ParticipantId(String participantId);
    //List<Participant> findAllByEventIdentifier_EventId(String eventId);
   // Participant findParticipantByEventIdentifier_EventIdAndParticipantIdentifier_ParticipantId(String eventId, String participantId);
}
