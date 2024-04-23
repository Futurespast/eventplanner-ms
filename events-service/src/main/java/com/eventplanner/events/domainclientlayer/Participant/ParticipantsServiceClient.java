package com.eventplanner.events.domainclientlayer.Participant;

import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.utils.HttpErrorInfo;
import com.eventplanner.events.utils.InvalidInputException;
import com.eventplanner.events.utils.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
@Slf4j
public class ParticipantsServiceClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String PARTICIPANTS_SERVICE_BASE_URL;

    private ParticipantsServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${app.participants-service.host}") String participantsServiceHost,
                                      @Value("${app.participants-service.port}") String participantsServicePort)
    {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;

        PARTICIPANTS_SERVICE_BASE_URL = "http://" + participantsServiceHost + ":" + participantsServicePort + "/api/v1/participants";

    }

    public ParticipantModel getParticipantById(String participantId){
        try{
            String url = PARTICIPANTS_SERVICE_BASE_URL+"/"+participantId;

            ParticipantModel participantModel = restTemplate.getForObject(url, ParticipantModel.class);
            return participantModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
//include all possible responses from the client
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }
        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch (IOException ioex) {
            return ioex.getMessage();
        }
    }
}
