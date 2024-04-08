package com.eventplanner.participants.presentationlayer;

import com.eventplanner.participants.dataacesslayer.Participant;
import com.eventplanner.participants.dataacesslayer.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ParticipantControllerIntegrationTest {



        private final String BASE_URI_PARTICIPANTS = "/api/v1/participants";
        private final String FOUND_PARTICIPANT_ID = "25a249e0-52c1-4911-91e2-b50fffef55e6";
        private final String FOUND_PARTICIPANT_FIRST_NAME = "John";
        private final String FOUND_PARTICIPANT_LAST_NAME = "Doe";
        private final String FOUND_PARTICIPANT_EMAIL = "john.doe@example.com";
        private final String FOUND_PARTICIPANT_SPECIALNOTE = "Vegetarian";
        private final String NOT_FOUND_PARTICIPANT_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";
        private final String INVALID_PARTICIPANT_ID = "c3540a89-cb47-4c96-888e-ff96708db";

        @Autowired
        private ParticipantRepository participantRepository;

        @Autowired
        private WebTestClient webTestClient;



        @Test
        public void whenGetParticipants_thenReturnALLParticipants(){
            //arrange
            long sizeDB = participantRepository.count();

            //act and assert
            webTestClient.get().uri(BASE_URI_PARTICIPANTS).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(ParticipantResponseModel.class).value((list)->{
                        assertNotNull(list);
                        assertTrue(list.size() == sizeDB);
                    })
                    .hasSize(20);
        }

        @Test
        public void whenParticipantDoesNotExist_thenReturnNotFound(){
            //act + assert
            webTestClient.get().uri(BASE_URI_PARTICIPANTS+"/"+NOT_FOUND_PARTICIPANT_ID).accept(MediaType.APPLICATION_JSON)
                    .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                    .jsonPath("$.message").isEqualTo("Participant id does not exist:"+ NOT_FOUND_PARTICIPANT_ID);
        }

    @Test
    public void whenParticipantExists_thenReturnParticipantDetails() {
        // Arrange
        String existingParticipantId = FOUND_PARTICIPANT_ID;


        // Act + Assert
        webTestClient.get().uri(BASE_URI_PARTICIPANTS + "/" + existingParticipantId).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.participantId").isEqualTo(existingParticipantId)
                .jsonPath("$.firstName").isEqualTo(FOUND_PARTICIPANT_FIRST_NAME)
                .jsonPath("$.lastName").isEqualTo(FOUND_PARTICIPANT_LAST_NAME)
                .jsonPath("$.emailAddress").isEqualTo(FOUND_PARTICIPANT_EMAIL)
                .jsonPath("$.specialNote").isEqualTo(FOUND_PARTICIPANT_SPECIALNOTE);
    }

    @Test
    public void WhenUpdateParticipantValid_thenReturnUpdatedParticipant() {
        // Arrange
        String participantId = "25a249e0-52c1-4911-91e2-b50fffef55e6";
        ParticipantRequestModel updateRequest = new ParticipantRequestModel("John", "Doe", "john.doe@example.com", "Updated note");


        // Act & Assert
        webTestClient.put().uri(BASE_URI_PARTICIPANTS + "/" + participantId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParticipantResponseModel.class).value((participantResponseModel -> {
                    assertNotNull(participantResponseModel);
                    assertEquals(updateRequest.getFirstName(), participantResponseModel.getFirstName());
                    assertEquals(updateRequest.getLastName(), participantResponseModel.getLastName());
                    assertEquals(updateRequest.getEmailAddress(),participantResponseModel.getEmailAddress());
                    assertEquals(updateRequest.getSpecialNote(),participantResponseModel.getSpecialNote());
                }));

    }

    @Test
    public void updateParticipantDoesNotExist_ThrowNotFound() {
        // Arrange
        String nonExistentParticipantId = "112432dhxf-24";
        ParticipantRequestModel updateRequest = new ParticipantRequestModel("Jane", "Smith", "jane.smith@example.com", "No allergies");


        // Act & Assert
        webTestClient.put().uri(BASE_URI_PARTICIPANTS + "/" + nonExistentParticipantId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Participant id does not exist:" + nonExistentParticipantId);
    }

    @Test
    public void updateParticipant_InvalidEmail() {
        // Arrange
        String participantId = FOUND_PARTICIPANT_ID;
        ParticipantRequestModel updateRequest = new ParticipantRequestModel("Invalid", "Email", "invalid-email", "Special note");


        // Act & Assert
        webTestClient.put().uri(BASE_URI_PARTICIPANTS + "/" + participantId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("Invalid email address provided: "+updateRequest.getEmailAddress());
    }



    @Test
        public void whenValidParticipant_thenCreateParticipant() {
            //arrange
            long sizeDB = participantRepository.count();
            ParticipantRequestModel participantRequestModel = new ParticipantRequestModel("Mac", "Miller", "email@email.com", "none");

            webTestClient.post().uri(BASE_URI_PARTICIPANTS).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .bodyValue(participantRequestModel).exchange().expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(ParticipantResponseModel.class).value((participantResponseModel -> {
                        assertNotNull(participantResponseModel);
                        assertEquals(participantRequestModel.getFirstName(), participantResponseModel.getFirstName());
                        assertEquals(participantRequestModel.getLastName(), participantResponseModel.getLastName());
                        assertEquals(participantRequestModel.getEmailAddress(),participantResponseModel.getEmailAddress());
                    }));
            long sizeDBAfter = participantRepository.count();
            assertEquals(sizeDB + 1, sizeDBAfter );

        }

    @Test
    public void whenValidParticipantButInvalidEmail_thenUnprocessableEntity() {
        // Arrange
        long sizeDB = participantRepository.count();
        ParticipantRequestModel participantRequestModel = new ParticipantRequestModel("Mac", "Miller", "invalid-email", "none");

        // Act & Assert
        webTestClient.post().uri(BASE_URI_PARTICIPANTS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(participantRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("Invalid email address provided: invalid-email");

        long sizeDBAfter = participantRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
        }

    @Test
    public void deleteParticipantThatExist_ReturnNoContent() {
        // Arrange
        String existingParticipantId = FOUND_PARTICIPANT_ID;
        long sizeDB = participantRepository.count();

        // Act & Assert
        webTestClient.delete().uri(BASE_URI_PARTICIPANTS + "/" + existingParticipantId)
                .exchange()
                .expectStatus().isNoContent();

        long sizeDBAfter = participantRepository.count();
        assertEquals(sizeDB - 1, sizeDBAfter );

    }

    @Test
    public void deleteParticipant_NotFound() {
        // Arrange
        String nonExistentParticipantId = "q13232shd2";
        long sizeDB = participantRepository.count();
        // Act & Assert
        webTestClient.delete().uri(BASE_URI_PARTICIPANTS + "/" + nonExistentParticipantId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Participant id does not exist:" + nonExistentParticipantId);

        long sizeDBAfter = participantRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
    }


}

