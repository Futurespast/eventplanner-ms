package com.eventplanner.customers.presentationlayer;

import com.eventplanner.customers.businesslayer.CustomerService;
import com.eventplanner.customers.dataacesslayer.CustomerIdentifier;
import com.eventplanner.customers.dataacesslayer.PhoneNumber;
import com.eventplanner.customers.dataacesslayer.PhoneType;
import com.eventplanner.customers.utils.InvalidCustomerNameException;
import com.eventplanner.customers.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = CustomerController.class)
class CustomerControllerUnitTest {
    private final String FOUND_CUSTOMER_ID = "c3540a89-cb47-4c96-888e-ff96708db4d8";
    private final String NOT_FOUND_CUSTOMER_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";
    private final String INVALID_CUSTOMER_ID = "23djwsdjw-a";

    @Autowired
    CustomerController customerController;

    @MockBean
    private CustomerService customerService;

    @Test
    public void whenNoCustomerExists_ThenReturnEmptyList(){
        //arrange
        when(customerService.getCustomers()).thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<List<CustomerResponseModel>> responseEntity = customerController.getCustomers();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());
        verify(customerService, times(1)).getCustomers();
    }

    @Test
    public void whenCustomerExists_thenReturnCustomer(){
        CustomerRequestModel customerRequestModel = buildCustomerRequestModel();
        CustomerResponseModel customerResponseModel = buildCustomerResponseModel();

        when(customerService.addCustomer(customerRequestModel)).thenReturn(customerResponseModel);

        ResponseEntity<CustomerResponseModel> responseEntity = customerController.addCustomer(customerRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(customerResponseModel, responseEntity.getBody());
        verify(customerService, times(1)).addCustomer(customerRequestModel);
    }

    @Test
    public void whenCustomerDoesNotExist_thenRespondWithNotFound() {
        // Given
        String nonExistentCustomerId = UUID.randomUUID().toString();
        doThrow(new NotFoundException("Customer not found with ID: " + nonExistentCustomerId))
                .when(customerService).getCustomerByCustomerId(nonExistentCustomerId);

        // When
        ResponseEntity<CustomerResponseModel> responseEntity = null;
        NotFoundException thrownException = null;
        try {
            responseEntity = customerController.getCustomerByCustomerId(nonExistentCustomerId);
        } catch (NotFoundException e) {
            thrownException = e;
        }

        // Then
        assertNotNull(thrownException, "Expected NotFoundException to be thrown");
        assertNull(responseEntity, "ResponseEntity should be null when an exception is thrown");
        verify(customerService, times(1)).getCustomerByCustomerId(nonExistentCustomerId);
    }



    @Test
    public void postCustomerTest(){
        CustomerRequestModel customerRequestModel = buildCustomerRequestModel();
        CustomerResponseModel customerResponseModel = buildCustomerResponseModel();

        when(customerService.addCustomer(customerRequestModel)).thenReturn(customerResponseModel);

        ResponseEntity<CustomerResponseModel> responseEntity = customerController.addCustomer(customerRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(customerResponseModel, responseEntity.getBody());
        verify(customerService, times(1)).addCustomer(customerRequestModel);
    }

    @Test
    public void postCustomerTest_InvalidCustomerNameException() {
        // Given
        CustomerRequestModel customerRequestModel = buildCustomerRequestModel();
        customerRequestModel.setFirstName("@!");


        when(customerService.addCustomer(any(CustomerRequestModel.class)))
                .thenThrow(new InvalidCustomerNameException(customerRequestModel.getFirstName()+" "+customerRequestModel.getLastName()));

        // When
        Exception exception = assertThrows(InvalidCustomerNameException.class, () -> {
           customerController.addCustomer(customerRequestModel);
        });

        // Then
        assertTrue(exception.getMessage().contains("Invalid customer name provided: "+customerRequestModel.getFirstName()+" "+customerRequestModel.getLastName()));
        verify(customerService, times(1)).addCustomer(customerRequestModel);
    }

    @Test
    public void updateCustomerTest_Positive() {
        String customerId = "c3540a89-cb47-4c96-888e-ff96708db4d8";
        CustomerRequestModel customerRequestModel = buildCustomerRequestModel();
        CustomerResponseModel expectedResponse = buildCustomerResponseModel();

        when(customerService.updateCustomer(customerRequestModel, customerId)).thenReturn(expectedResponse);

        ResponseEntity<CustomerResponseModel> responseEntity = customerController.updateCustomer(customerRequestModel, customerId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(customerService, times(1)).updateCustomer(customerRequestModel, customerId);
    }

    @Test
    public void updateCustomerTest_Negative() {
        String customerId = UUID.randomUUID().toString();
        CustomerRequestModel customerRequestModel = buildCustomerRequestModel();

        when(customerService.updateCustomer(customerRequestModel, customerId))
                .thenThrow(new NotFoundException("Customer id does not exist:" + customerId));

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            customerController.updateCustomer(customerRequestModel, customerId);
        });

        assertTrue(thrownException.getMessage().contains("Customer id does not exist:"+customerId));
        verify(customerService, times(1)).updateCustomer(customerRequestModel, customerId);
    }

    @Test
    public void deleteCustomerTest_Positive() {
        CustomerIdentifier customerIdentifier = new CustomerIdentifier("c3540a89-cb47-4c96-888e-ff96708db4d8");


        doNothing().when(customerService).removeCustomer(customerIdentifier.getCustomerId());

        ResponseEntity<Void> responseEntity = customerController.deleteCustomer(customerIdentifier.getCustomerId());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(customerService, times(1)).removeCustomer(customerIdentifier.getCustomerId());
    }

    @Test
    public void deleteCustomerTest_Negative() {
        String customerId = UUID.randomUUID().toString();

        doThrow(new NotFoundException("Customer id does not exist:" + customerId))
                .when(customerService).removeCustomer(customerId);

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
           customerController.deleteCustomer(customerId);
        });

        assertTrue(thrownException.getMessage().contains("Customer id does not exist:"+ customerId));
        verify(customerService, times(1)).removeCustomer(customerId);
    }



    private CustomerRequestModel buildCustomerRequestModel(){
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(PhoneType.HOME, "514-555-5555"));
        phoneNumbers.add(new PhoneNumber(PhoneType.MOBILE, "514-475-5555"));
        // CustomerRequestModel customerRequestModel = new CustomerRequestModel("Mac", "Miller", "macmil@gmail.com", "11 street", "City", "Prov", "Canada", "AAAAAA", phoneNumbers);

        return CustomerRequestModel.builder()
                .firstName("Mac")
                .lastName("Miller")
                .emailAddress("macmil@gmail.com")
                .streetAddress("11 street")
                .city("city")
                .province("quebec")
                .country("canada")
                .postalCode("aaaaaa")
                .phoneNumbers(phoneNumbers)
                .build();
    }

    private CustomerResponseModel buildCustomerResponseModel(){
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(PhoneType.HOME, "514-555-5555"));
        phoneNumbers.add(new PhoneNumber(PhoneType.MOBILE, "514-475-5555"));
        // CustomerRequestModel customerRequestModel = new CustomerRequestModel("Mac", "Miller", "macmil@gmail.com", "11 street", "City", "Prov", "Canada", "AAAAAA", phoneNumbers);

        return CustomerResponseModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("Mac")
                .lastName("Miller")
                .emailAddress("macmil@gmail.com")
                .streetAddress("11 street")
                .city("city")
                .province("quebec")
                .country("canada")
                .postalCode("aaaaaa")
                .phoneNumbers(phoneNumbers)
                .build();
    }

}