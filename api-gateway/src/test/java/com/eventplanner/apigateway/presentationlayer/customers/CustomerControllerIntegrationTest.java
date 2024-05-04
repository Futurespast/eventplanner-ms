package com.eventplanner.apigateway.presentationlayer.customers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void init(){
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

    }

    private final String CUSTOMERS_SERVICE_BASE_URL = "/api/v1/customers";

    private final String MOCKSERVICE_BASE_URL = "http://localhost:7001/api/v1/customers";

    CustomerResponseModel customerResponseModel = new CustomerResponseModel("1","John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>());

    CustomerRequestModel customerRequestModel = new CustomerRequestModel("John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>());

    @Test
    public void whenGetAllCustomers_ReturnAllCustomers() throws JsonProcessingException, URISyntaxException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(MOCKSERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(List.of(customerResponseModel)), MediaType.APPLICATION_JSON));
    webTestClient.get().uri(CUSTOMERS_SERVICE_BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponseModel.class).value(lists -> {
                    assertNotNull(lists);
                    assertEquals(1,lists.size());
                });
    }


    @Test
    public void whenGetCustomerById_ReturnCustomerById() throws JsonProcessingException, URISyntaxException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andRespond(withSuccess(mapper.writeValueAsString(customerResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class).value(customer -> {
                    assertNotNull(customer);
                    assertEquals("1",customer.getCustomerId());
                    assertEquals("John",customer.getFirstName());
                    assertEquals("Doe",customer.getLastName());
                    assertEquals("asq@gmail",customer.getEmailAddress());
                    assertEquals("address",customer.getStreetAddress());
                    assertEquals("city",customer.getCity());
                    assertEquals("state",customer.getProvince());
                    assertEquals("country",customer.getCountry());
                    assertEquals("zip",customer.getPostalCode());
                });
    }

    @Test
    public void whenGetCustomerByInvalidId_ReturnNotFound() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString("Customer not found")));
        webTestClient.get().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectBody().jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");
    }

    @Test
    public void whenAddCustomer_ReturnAddedCustomer() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(customerResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.post().uri(CUSTOMERS_SERVICE_BASE_URL)
                .bodyValue(customerRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseModel.class).value(customer -> {
                    assertNotNull(customer);
                    assertEquals("1", customer.getCustomerId());
                    assertEquals("John", customer.getFirstName());
                    assertEquals("Doe", customer.getLastName());
                    assertEquals("asq@gmail", customer.getEmailAddress());
                    assertEquals("address", customer.getStreetAddress());
                    assertEquals("city", customer.getCity());
                    assertEquals("state", customer.getProvince());
                    assertEquals("country", customer.getCountry());
                    assertEquals("zip", customer.getPostalCode());
                });
    }

    @Test
    public void whenAddCustomerWithInvalidId_ReturnNotFound() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL)))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString("Customer not found")));
        webTestClient.post().uri(CUSTOMERS_SERVICE_BASE_URL)
                .bodyValue(customerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectBody().jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");
    }

    @Test
    public void whenUpdateCustomer_ReturnUpdatedCustomer() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(mapper.writeValueAsString(customerResponseModel), MediaType.APPLICATION_JSON));
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL + "/1"))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(customerResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.put().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .bodyValue(customerRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class).value(customer -> {
                    assertNotNull(customer);
                    assertEquals("1", customer.getCustomerId());
                    assertEquals("John", customer.getFirstName());
                    assertEquals("Doe", customer.getLastName());
                    assertEquals("asq@gmail", customer.getEmailAddress());
                    assertEquals("address", customer.getStreetAddress());
                    assertEquals("city", customer.getCity());
                    assertEquals("state", customer.getProvince());
                    assertEquals("country", customer.getCountry());
                    assertEquals("zip", customer.getPostalCode());
                });
    }
    @Test
    public void whenUpdateCustomerWithInvalidId_ReturnNotFound() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString("Customer not found")));
        webTestClient.put().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .bodyValue(customerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectBody().jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");
    }
    @Test
    public void whenDeleteCustomer_ReturnNoContent() throws URISyntaxException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        webTestClient.delete().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void whenDeleteCustomerWithInvalidId_ReturnNotFound() throws URISyntaxException, JsonProcessingException {
        //act
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(MOCKSERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString("Customer not found")));
        webTestClient.delete().uri(CUSTOMERS_SERVICE_BASE_URL + "/1")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectBody().jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");
    }

    @Test
    public void WhenGetAllGetsAnError(){
        mockRestServiceServer.expect(requestTo(MOCKSERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(CUSTOMERS_SERVICE_BASE_URL).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

