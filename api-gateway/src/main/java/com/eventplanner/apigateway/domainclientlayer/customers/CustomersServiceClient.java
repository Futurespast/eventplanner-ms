package com.eventplanner.apigateway.domainclientlayer.customers;



import com.eventplanner.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerResponseModel;
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
public class CustomersServiceClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final String CUSTOMERS_SERVICE_BASE_URL;

    private CustomersServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${app.customers-service.host}") String customerServiceHost,
                                   @Value("${app.customers-service.port}") String customersServicePort)
    {
    this.restTemplate = restTemplate;
    this.mapper = objectMapper;

    CUSTOMERS_SERVICE_BASE_URL = "http://" + customerServiceHost + ":" + customersServicePort + "/api/v1/customers";

    }

   public List<CustomerResponseModel> getAllCustomers(){
        try{
            String url = CUSTOMERS_SERVICE_BASE_URL;
          CustomerResponseModel[] customerModels = restTemplate.getForObject(url, CustomerResponseModel[].class);
          return Arrays.asList(customerModels);
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }



        public CustomerResponseModel getCustomerByCustomerId(String customerId){
        try{
            String url = CUSTOMERS_SERVICE_BASE_URL + "/" +customerId;
          CustomerResponseModel customerModel = restTemplate.getForObject(url, CustomerResponseModel.class);
          return customerModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel){
        try{
            String url = CUSTOMERS_SERVICE_BASE_URL;
            CustomerResponseModel customerModel = restTemplate.postForObject(url,customerRequestModel,CustomerResponseModel.class);
            return customerModel;
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

   public void updateCustomer(CustomerRequestModel customerRequestModel, String customerId){
        try{
            String url = CUSTOMERS_SERVICE_BASE_URL + "/" +customerId;
            restTemplate.put(url,customerRequestModel);
        } catch (HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
   }

   public void deleteCustomer(String customerId){
       try{
           String url = CUSTOMERS_SERVICE_BASE_URL + "/" +customerId;
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
