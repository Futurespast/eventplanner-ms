package com.eventplanner.participants.presentationlayer;

import com.eventplanner.participants.businesslayer.ParticipantService;
import com.eventplanner.participants.dataacesslayer.Participant;
import com.eventplanner.participants.dataacesslayer.ParticipantIdentifier;
import com.eventplanner.participants.utils.InvalidEmailAddressException;
import com.eventplanner.participants.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = ParticipantController.class)
class ParticipantControllerUnitTest {
    private final String FOUND_PARTICIPANT_ID = "25a249e0-52c1-4911-91e2-b50fffef55e6";
    private final String NOT_FOUND_PARTICIPANT_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";
    private final String INVALID_PARTICIPANT_ID = "23djwsdjw-a";

    @Autowired
    ParticipantController participantController;

    @MockBean
    private ParticipantService participantService;

    @Test
    public void whenNoParticipantExists_ThenReturnEmptyList(){
        //arrange
        when(participantService.getParticipants()).thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<List<ParticipantResponseModel>> responseEntity = participantController.getParticipants();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());
        verify(participantService, times(1)).getParticipants();
    }

    @Test
    public void whenParticipantExists_thenReturnParticipant() {
        // Given
        ParticipantResponseModel participantResponseModel = buildParticipantResponseModel();
        when(participantService.getParticipantById("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(participantResponseModel);

        // When
        ResponseEntity<ParticipantResponseModel> responseEntity = participantController.getParticipantById("c3540a89-cb47-4c96-888e-ff96708db4d8");

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(participantResponseModel, responseEntity.getBody());
        verify(participantService, times(1)).getParticipantById("c3540a89-cb47-4c96-888e-ff96708db4d8");
    }

    @Test
    public void whenParticipantDoesNotExist_thenRespondWithNotFound() {
        // Given
        String nonExistentParticipantId = UUID.randomUUID().toString();
        doThrow(new NotFoundException("Participant not found with ID: " + nonExistentParticipantId))
                .when(participantService).getParticipantById(nonExistentParticipantId);

        // When
        ResponseEntity<ParticipantResponseModel> responseEntity = null;
        NotFoundException thrownException = null;
        try {
            responseEntity = participantController.getParticipantById(nonExistentParticipantId);
        } catch (NotFoundException e) {
            thrownException = e;
        }

        // Then
        assertNotNull(thrownException, "Expected NotFoundException to be thrown");
        assertNull(responseEntity, "ResponseEntity should be null when an exception is thrown");
        verify(participantService, times(1)).getParticipantById(nonExistentParticipantId);
    }



    @Test
    public void postParticipantTest(){
        ParticipantRequestModel participantRequestModel = buildParticipantRequestModel();
        ParticipantResponseModel participantResponseModel = buildParticipantResponseModel();

        when(participantService.addParticipant(participantRequestModel)).thenReturn(participantResponseModel);

        ResponseEntity<ParticipantResponseModel> responseEntity = participantController.addParticipant(participantRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(participantResponseModel, responseEntity.getBody());
        verify(participantService, times(1)).addParticipant(participantRequestModel);
    }

    @Test
    public void postParticipantTest_InvalidEmail() {
        // Given
        ParticipantRequestModel participantRequestModel = buildParticipantRequestModel();
        participantRequestModel.setEmailAddress("wee@");


        when(participantService.addParticipant(any(ParticipantRequestModel.class)))
                .thenThrow(new InvalidEmailAddressException(participantRequestModel.getEmailAddress()));

        // When
        Exception exception = assertThrows(InvalidEmailAddressException.class, () -> {
            participantController.addParticipant(participantRequestModel);
        });

        // Then
        assertTrue(exception.getMessage().contains("Invalid email address provided: " + participantRequestModel.getEmailAddress()));
        verify(participantService, times(1)).addParticipant(participantRequestModel);
    }

    @Test
    public void updateParticipantTest_Positive() {
        String participantId = "c3540a89-cb47-4c96-888e-ff96708db4d8";
        ParticipantRequestModel participantRequestModel = buildParticipantRequestModel();
        ParticipantResponseModel expectedResponse = buildParticipantResponseModel();

        when(participantService.updateParticipant(participantRequestModel, participantId)).thenReturn(expectedResponse);

        ResponseEntity<ParticipantResponseModel> responseEntity = participantController.updateParticipant(participantId,participantRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(participantService, times(1)).updateParticipant(participantRequestModel, participantId);
    }

    @Test
    public void updateParticipantTest_Negative() {
        String participantId = UUID.randomUUID().toString();
        ParticipantRequestModel participantRequestModel = buildParticipantRequestModel();

        when(participantService.updateParticipant(participantRequestModel, participantId))
                .thenThrow(new NotFoundException("Participant id does not exist:" + participantId));

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            participantController.updateParticipant( participantId, participantRequestModel);
        });

        assertTrue(thrownException.getMessage().contains("Participant id does not exist:"+participantId));
        verify(participantService, times(1)).updateParticipant(participantRequestModel, participantId);
    }

    @Test
    public void deleteParticipantTest_Positive() {
        ParticipantIdentifier participantIdentifier = new ParticipantIdentifier("c3540a89-cb47-4c96-888e-ff96708db4d8");


        doNothing().when(participantService).deleteParticipant(participantIdentifier.getParticipantId());

        ResponseEntity<Void> responseEntity = participantController.deleteParticipant(participantIdentifier.getParticipantId());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(participantService, times(1)).deleteParticipant(participantIdentifier.getParticipantId());
    }

    @Test
    public void deleteParticipantTest_Negative() {
        String participantId = UUID.randomUUID().toString();

        doThrow(new NotFoundException("Participant id does not exist:" + participantId))
                .when(participantService).deleteParticipant(participantId);

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            participantController.deleteParticipant(participantId);
        });

        assertTrue(thrownException.getMessage().contains("Participant id does not exist:"+ participantId));
        verify(participantService, times(1)).deleteParticipant(participantId);
    }


    private ParticipantRequestModel buildParticipantRequestModel(){


        return ParticipantRequestModel.builder()
                .firstName("Mac")
                .lastName("Miller")
                .emailAddress("macmil@gmail.com")
                .specialNote("none")
                .build();
    }

    private ParticipantResponseModel buildParticipantResponseModel(){


        return ParticipantResponseModel.builder()
                .participantId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("Mac")
                .lastName("Miller")
                .emailAddress("macmil@gmail.com")
                .specialNote("none")
                .build();
    }
}

