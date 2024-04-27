package com.eventplanner.apigateway.domainclientlayer.venues;


import com.eventplanner.apigateway.presentationlayer.venues.VenueRequestModel;
import com.eventplanner.apigateway.presentationlayer.venues.VenueResponseModel;
import com.eventplanner.apigateway.utils.HttpErrorInfo;
import com.eventplanner.apigateway.utils.InvalidInputException;
import com.eventplanner.apigateway.utils.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
@Slf4j
public class VenuesServiceClient {
    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String VENUES_SERVICE_BASE_URL;

    private VenuesServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${app.venues-service.host}") String venuesServiceHost,
                                @Value("${app.venues-service.port}") String venuesServicePort)
    {
        this.restTemplate = restTemplate;
        this.mapper = objectMapper;

        VENUES_SERVICE_BASE_URL = "http://" + venuesServiceHost + ":" + venuesServicePort + "/api/v1/venues";

    }

    public VenueResponseModel getVenueByVenueId(String venueId){
        try{
            String url = VENUES_SERVICE_BASE_URL + "/" +venueId;
            VenueResponseModel venueModel = restTemplate.getForObject(url, VenueResponseModel.class);
            return venueModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public List<VenueResponseModel> getVenues(){
        try{
            String url = VENUES_SERVICE_BASE_URL;
            VenueResponseModel[] venueModel = restTemplate.getForObject(url, VenueResponseModel[].class);
            return Arrays.asList(venueModel);
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public VenueResponseModel addVenue(VenueRequestModel venueRequestModel){
        try{
            String url = VENUES_SERVICE_BASE_URL;
            VenueResponseModel venueModel = restTemplate.postForObject(url,venueRequestModel,VenueResponseModel.class);
            return venueModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public void updateVenue(VenueRequestModel venueRequestModel,String venueId){
        try{
            String url = VENUES_SERVICE_BASE_URL + "/" +venueId;
            restTemplate.put(url,venueRequestModel);

        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public void deleteVenue(String venueId){
        try{
            String url = VENUES_SERVICE_BASE_URL + "/" +venueId;
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
