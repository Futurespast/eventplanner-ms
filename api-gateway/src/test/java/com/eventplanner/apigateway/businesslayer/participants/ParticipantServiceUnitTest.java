package com.eventplanner.apigateway.businesslayer.participants;

import com.eventplanner.apigateway.datamapperlayer.participants.ParticipantResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.participants.ParticipantsServiceClient;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantRequestModel;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantResponseModel;
import com.eventplanner.apigateway.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ParticipantServiceUnitTest {

    @Autowired
    ParticipantService participantService;

    @MockBean
    ParticipantsServiceClient participantsServiceClient;

    @SpyBean
    ParticipantResponseMapper participantResponseMapper;

    ParticipantResponseModel participantResponseModel = new ParticipantResponseModel("1", "John", "Doe", "p@gamil.com", "note");

    ParticipantRequestModel participantRequestModel = new ParticipantRequestModel("John", "Doe", "p@gmail.com", "note");

    @Test
    public void GetAllParticipants(){
        when(participantsServiceClient.getAllParticipants()).thenReturn(List.of(participantResponseModel));
        List<ParticipantResponseModel> participantResponseModels = participantService.getAllParticipants();
        assertNotNull(participantResponseModels);
        assertEquals(1,participantResponseModels.size());
    }

    @Test
    public void GetParticipantById() {
        when(participantsServiceClient.getParticipantById("1")).thenReturn(participantResponseModel);
        ParticipantResponseModel participantResponseModel = participantService.getParticipantById("1");
        assertNotNull(participantResponseModel);
        assertEquals("1", participantResponseModel.getParticipantId());
        assertEquals("John", participantResponseModel.getFirstName());
        assertEquals("Doe", participantResponseModel.getLastName());
        assertEquals("p@gamil.com", participantResponseModel.getEmailAddress());
        assertEquals("note", participantResponseModel.getSpecialNote());
    }

    @Test
    public void WhenInvalidIdForGet(){
        when(participantsServiceClient.getParticipantById("1")).thenThrow(new NotFoundException("Participant not found"));
        assertThrows(NotFoundException.class, () -> participantService.getParticipantById("1"));
    }

    @Test
    public void AddParticipant() {
        when(participantsServiceClient.addParticipant(participantRequestModel)).thenReturn(participantResponseModel);
        ParticipantResponseModel participantResponseModel = participantService.addParticipant(participantRequestModel);
        assertNotNull(participantResponseModel);
        assertEquals("1", participantResponseModel.getParticipantId());
        assertEquals("John", participantResponseModel.getFirstName());
        assertEquals("Doe", participantResponseModel.getLastName());
        assertEquals("p@gamil.com", participantResponseModel.getEmailAddress());
        assertEquals("note", participantResponseModel.getSpecialNote());
    }

    @Test
    public void WhenInvalidIdForAdd() {
        when(participantsServiceClient.addParticipant(participantRequestModel)).thenThrow(new NotFoundException("Participant not found"));
        assertThrows(NotFoundException.class, () -> participantService.addParticipant(participantRequestModel));
    }

    @Test
    public void UpdateParticipant(){
        doNothing().when(participantsServiceClient).updateParticipant(participantRequestModel, "1");
        participantService.updateParticipant(participantRequestModel, "1");
    }

    @Test
    public void WhenInvalidIdForUpdate(){
        doThrow(new NotFoundException("Participant not found")).when(participantsServiceClient).updateParticipant(participantRequestModel, "1");
        assertThrows(NotFoundException.class, () -> participantService.updateParticipant(participantRequestModel, "1"));
    }

    @Test
    public void DeleteParticipant(){
        doNothing().when(participantsServiceClient).deleteParticipant("1");
        participantService.deleteParticipant("1");
    }

    @Test
    public void WhenInvalidIdForDelete(){
       doThrow(new NotFoundException("Participant not found")).when(participantsServiceClient).deleteParticipant("1");
        assertThrows(NotFoundException.class, () -> participantService.deleteParticipant("1"));
    }

}