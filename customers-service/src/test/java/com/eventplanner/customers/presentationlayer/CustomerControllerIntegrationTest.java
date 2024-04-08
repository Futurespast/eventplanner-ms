package com.eventplanner.customers.presentationlayer;



import com.eventplanner.customers.dataacesslayer.Address;
import com.eventplanner.customers.dataacesslayer.CustomerRepository;
import com.eventplanner.customers.dataacesslayer.PhoneNumber;
import com.eventplanner.customers.dataacesslayer.PhoneType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
class CustomerControllerIntegrationTest {
    private final String BASE_URI_CUSTOMERS = "/api/v1/customers";
    private final String FOUND_CUSTOMER_ID = "c3540a89-cb47-4c96-888e-ff96708db4d8";
    private final String FOUND_CUSTOMER_FIRST_NAME = "John";
    private final String FOUND_CUSTOMER_LAST_NAME = "Doe";

    private  final String FOUND_CUSTOMER_EMAIL = "johndoe@example.com";

    private final Address FOUND_CUSTOMER_ADDRESS = new Address("123 Maple Street","Anytown","Ontario","Canada","A1B 2C3");
    private final ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
    private final String NOT_FOUND_CUSTOMER_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGetCustomers_thenReturnALLCustomers(){
        //arrange
        long sizeDB = customerRepository.count();

        //act and assert
        webTestClient.get().uri(BASE_URI_CUSTOMERS).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(CustomerResponseModel.class).value((list)->{
                    assertNotNull(list);
                    assertTrue(list.size() == sizeDB);
                });

    }

    @Test
    public void whenCustomerDoesNotExist_thenReturnNotFound(){
        //act + assert
        webTestClient.get().uri(BASE_URI_CUSTOMERS+"/"+NOT_FOUND_CUSTOMER_ID).accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Unknown customerId: "+ NOT_FOUND_CUSTOMER_ID);
    }

    @Test
    public void whenValidCustomer_thenCreateCustomer() {
        //arrange
        long sizeDB = customerRepository.count();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(PhoneType.HOME, "514-555-5555"));
        phoneNumbers.add(new PhoneNumber(PhoneType.MOBILE, "514-475-5555"));
        CustomerRequestModel customerRequestModel = new CustomerRequestModel("Mac", "Miller", "macmil@gmail.com", "11 street", "City", "Prov", "Canada", "AAAAAA", phoneNumbers);

        webTestClient.post().uri(BASE_URI_CUSTOMERS).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequestModel).exchange().expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerResponseModel.class).value((customerResponseModel -> {
                    assertNotNull(customerResponseModel);
                    assertEquals(customerRequestModel.getFirstName(), customerResponseModel.getFirstName());
                    assertEquals(customerRequestModel.getLastName(), customerResponseModel.getLastName());
                    assertEquals(customerRequestModel.getEmailAddress(),customerResponseModel.getEmailAddress());
                    assertEquals(customerRequestModel.getStreetAddress(), customerResponseModel.getStreetAddress());
                    assertEquals(customerRequestModel.getCity(), customerResponseModel.getCity());
                    assertEquals(customerRequestModel.getProvince(), customerResponseModel.getProvince());
                    assertEquals(customerRequestModel.getCountry(), customerResponseModel.getCountry());
                    assertEquals(customerRequestModel.getPostalCode(), customerResponseModel.getPostalCode());
                    assertEquals(customerRequestModel.getPhoneNumbers().get(0),phoneNumbers.get(0));
                    assertEquals(customerRequestModel.getPhoneNumbers().get(1),phoneNumbers.get(1));

                }));
        long sizeDBAfter = customerRepository.count();
        assertEquals(sizeDB + 1, sizeDBAfter );
    }

    @Test
    public void whenCustomerExists_thenReturnCustomerDetails() {
        // Arrange
        String foundCustomerId = FOUND_CUSTOMER_ID;
        phoneNumbers.add(new PhoneNumber(PhoneType.MOBILE,"555-1234"));
        phoneNumbers.add(new PhoneNumber(PhoneType.HOME,"555-5678"));

        // Act + Assert
        webTestClient.get().uri(BASE_URI_CUSTOMERS + "/" + foundCustomerId).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerResponseModel.class).value((customerResponseModel -> {

                    assertNotNull(customerResponseModel);
                    assertEquals(FOUND_CUSTOMER_FIRST_NAME, customerResponseModel.getFirstName());
                    assertEquals(FOUND_CUSTOMER_LAST_NAME, customerResponseModel.getLastName());
                    assertEquals(FOUND_CUSTOMER_EMAIL,customerResponseModel.getEmailAddress());
                    assertEquals(FOUND_CUSTOMER_ADDRESS.getStreetAddress(), customerResponseModel.getStreetAddress());
                    assertEquals(FOUND_CUSTOMER_ADDRESS.getCity(), customerResponseModel.getCity());
                    assertEquals(FOUND_CUSTOMER_ADDRESS.getProvince(), customerResponseModel.getProvince());
                    assertEquals(FOUND_CUSTOMER_ADDRESS.getCountry(), customerResponseModel.getCountry());
                    assertEquals(FOUND_CUSTOMER_ADDRESS.getPostalCode(), customerResponseModel.getPostalCode());
                }));
    }

    @Test
    public void WhenUpdateCustomerValid_thenReturnUpdatedCustomer() {
        // Arrange
        String customerId = FOUND_CUSTOMER_ID;
        phoneNumbers.add(new PhoneNumber(PhoneType.MOBILE,"555-1234"));
        phoneNumbers.add(new PhoneNumber(PhoneType.HOME,"555-5678"));
       CustomerRequestModel updateRequest = new CustomerRequestModel("John", "Doe", "johndoe@example.com", "idk", "city", "prov", "country", "111111",phoneNumbers);

        // Act & Assert
        webTestClient.put().uri(BASE_URI_CUSTOMERS+ "/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseModel.class).value((customerResponseModel -> {

                    assertNotNull(customerResponseModel);
                    assertEquals(updateRequest.getFirstName(), customerResponseModel.getFirstName());
                    assertEquals(updateRequest.getLastName(), customerResponseModel.getLastName());
                    assertEquals(updateRequest.getEmailAddress(),customerResponseModel.getEmailAddress());
                    assertEquals(updateRequest.getStreetAddress(), customerResponseModel.getStreetAddress());
                    assertEquals(updateRequest.getCity(), customerResponseModel.getCity());
                    assertEquals(updateRequest.getProvince(), customerResponseModel.getProvince());
                    assertEquals(updateRequest.getCountry(), customerResponseModel.getCountry());
                    assertEquals(updateRequest.getPostalCode(), customerResponseModel.getPostalCode());
                    assertEquals(updateRequest.getPhoneNumbers().get(0),phoneNumbers.get(0));
                    assertEquals(updateRequest.getPhoneNumbers().get(1),phoneNumbers.get(1));
                }));
    }

    @Test
    public void updateCustomerDoesNotExist_ThrowNotFound() {
        // Arrange
        String nonExistentCustomerId = "112432dhxf-24";
        CustomerRequestModel updateRequest = new CustomerRequestModel("John", "Doe", "johndoe@example.com", "idk", "city", "prov", "country", "111111",phoneNumbers);


        // Act & Assert
        webTestClient.put().uri(BASE_URI_CUSTOMERS + "/" + nonExistentCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Unknown customerId: " + nonExistentCustomerId);
    }

    @Test
    public void updateCustomer_InvalidName() {
        // Arrange
        String customerId = FOUND_CUSTOMER_ID;
        CustomerRequestModel updateRequest = new CustomerRequestModel("J!n", "Doe", "johndoe@example.com", "idk", "city", "prov", "country", "111111",phoneNumbers);


        // Act & Assert
        webTestClient.put().uri(BASE_URI_CUSTOMERS + "/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("Invalid customer name provided: "+updateRequest.getFirstName()+" "+updateRequest.getLastName());
    }


    @Test
    public void whenValidCustomerButInvalidName_thenUnprocessableEntity() {
        // Arrange
        long sizeDB = customerRepository.count();
       CustomerRequestModel customerRequestModel = new CustomerRequestModel("J!n", "Doe", "johndoe@example.com", "idk", "city", "prov", "country", "111111",phoneNumbers);

        // Act & Assert
        webTestClient.post().uri(BASE_URI_CUSTOMERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("Invalid customer name provided: "+customerRequestModel.getFirstName()+" "+customerRequestModel.getLastName());

        long sizeDBAfter = customerRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
    }

    @Test
    public void deleteCustomerThatExist_ReturnNoContent() {
        // Arrange
        String foundCustomerId = FOUND_CUSTOMER_ID;
        long sizeDB = customerRepository.count();

        // Act & Assert
        webTestClient.delete().uri(BASE_URI_CUSTOMERS + "/" + foundCustomerId)
                .exchange()
                .expectStatus().isNoContent();

        long sizeDBAfter = customerRepository.count();
        assertEquals(sizeDB - 1, sizeDBAfter );

    }

    @Test
    public void deleteCustomer_NotFound() {
        // Arrange
        String nonExistentCustomerId = "q13232shd2";
        long sizeDB = customerRepository.count();
        // Act & Assert
        webTestClient.delete().uri(BASE_URI_CUSTOMERS + "/" + nonExistentCustomerId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Unknown customerId: " + nonExistentCustomerId);

        long sizeDBAfter = customerRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
    }

}