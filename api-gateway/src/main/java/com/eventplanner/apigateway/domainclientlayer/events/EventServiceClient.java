package com.eventplanner.apigateway.domainclientlayer.events;

import com.eventplanner.apigateway.presentationlayer.events.EventRequestModel;
import com.eventplanner.apigateway.presentationlayer.events.EventResponseModel;
import com.eventplanner.apigateway.utils.HttpErrorInfo;
import com.eventplanner.apigateway.utils.InvalidInputException;
import com.eventplanner.apigateway.utils.NotFoundException;
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
public class EventServiceClient {
    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String EVENTS_SERVICE_BASE_URL;

    private EventServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${app.events-service.host}") String eventsServiceHost,
                                @Value("${app.events-service.port}") String eventsServicePort)
    {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;

        EVENTS_SERVICE_BASE_URL = "http://" + eventsServiceHost + ":" + eventsServicePort + "/api/v1/customers";

    }

    public EventResponseModel getEventByEventId(String customerId, String eventId){
        try{
            String url = EVENTS_SERVICE_BASE_URL + "/" +customerId+"/events/"+eventId;
            EventResponseModel eventResponseModel = restTemplate.getForObject(url, EventResponseModel.class);
            return eventResponseModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public List<EventResponseModel> getEvents(String customerId){
        try{
            String url = EVENTS_SERVICE_BASE_URL+"/"+customerId+"/events";
            EventResponseModel[] eventResponseModels = restTemplate.getForObject(url, EventResponseModel[].class);
            return Arrays.asList(eventResponseModels);
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public EventResponseModel addEvent(EventRequestModel eventRequestModel, String customerId){
        try{
            String url = EVENTS_SERVICE_BASE_URL+"/"+customerId+"/events";
            EventResponseModel eventResponseModel = restTemplate.postForObject(url,eventRequestModel,EventResponseModel.class);
            return eventResponseModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public void updateEvent(EventRequestModel eventRequestModel, String customerId, String eventId){
        try{
            String url = EVENTS_SERVICE_BASE_URL + "/" +customerId+"/events/"+eventId;
            restTemplate.put(url,eventRequestModel);

        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public void deleteEvent(String customerId, String eventId){
        try{
            String url = EVENTS_SERVICE_BASE_URL + "/" +customerId+"/events/"+eventId;
            restTemplate.delete(url);

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
