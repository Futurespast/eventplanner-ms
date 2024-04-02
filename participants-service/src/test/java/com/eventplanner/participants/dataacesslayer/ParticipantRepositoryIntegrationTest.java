package com.eventplanner.participants.dataacesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ParticipantRepositoryIntegrationTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    public void setUpDb(){participantRepository.deleteAll();}

    @Test
    public void whenParticipantExist_ReturnParticipantByParticipantId(){
        //arrange
        Participant participant = new Participant("John", "Doe", "Email", "none" );
        participantRepository.save(participant);
        //act
        Participant saved = participantRepository.findParticipantByParticipantIdentifier_ParticipantId(participant.getParticipantIdentifier().getParticipantId());
        //assert
        assertNotNull(participant);
        assertEquals(saved.getParticipantIdentifier(), participant.getParticipantIdentifier());
        assertEquals(saved.getFirstName(), participant.getFirstName());
        assertEquals(saved.getLastName(), participant.getLastName());
        assertEquals(saved.getSpecialNote(), participant.getSpecialNote());
        assertEquals(saved.getEmailAddress(),participant.getEmailAddress());
    }
    @Test
   public void whenParticipantDoesNotExist_ReturnNull(){
        //arrange
        String participantId = UUID.randomUUID().toString();
        //act
       Participant participant = participantRepository.findParticipantByParticipantIdentifier_ParticipantId(participantId);
       //assert
       assertNull(participant);
   }




}