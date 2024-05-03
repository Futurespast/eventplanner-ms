package com.eventplanner.apigateway.businesslayer.customers;

import com.eventplanner.apigateway.datamapperlayer.customers.CustomerResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.customers.CustomersServiceClient;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerResponseModel;
import com.eventplanner.apigateway.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceUnitTest {

    @Autowired
    CustomerService customerService;

    @MockBean
    CustomersServiceClient customersServiceClient;

    @SpyBean
    CustomerResponseMapper customerResponseMapper;

    @Test
    public void GetAllCustomers(){
        when(customersServiceClient.getAllCustomers()).thenReturn(List.of(new CustomerResponseModel("1","John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>())));
        List<CustomerResponseModel> customerResponseModels = customerService.getAllCustomers();
        assertNotNull(customerResponseModels);
        assertEquals(1,customerResponseModels.size());
    }

    @Test
    public void GetCustomerById(){
        when(customersServiceClient.getCustomerByCustomerId(any())).thenReturn(new CustomerResponseModel("1","John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>()));
        CustomerResponseModel customerResponseModel = customerService.getCustomerById("1");
        assertNotNull(customerResponseModel);
        assertEquals("1",customerResponseModel.getCustomerId());
        assertEquals("John",customerResponseModel.getFirstName());
        assertEquals("Doe",customerResponseModel.getLastName());
        assertEquals("asq@gmail",customerResponseModel.getEmailAddress());
        assertEquals("address",customerResponseModel.getStreetAddress());
        assertEquals("city",customerResponseModel.getCity());
        assertEquals("state",customerResponseModel.getProvince());
        assertEquals("country",customerResponseModel.getCountry());
        assertEquals("zip",customerResponseModel.getPostalCode());
    }

    @Test
    public void WhenCustomerIdInvalidForGet(){
        when(customersServiceClient.getCustomerByCustomerId(any())).thenThrow(new NotFoundException("Customerid provided is invalid"));
        assertThrows(NotFoundException.class,()->customerService.getCustomerById("1"));

    }

    @Test
    public void AddCustomer(){
        when(customersServiceClient.addCustomer(any())).thenReturn(new CustomerResponseModel("1","John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>()));
        CustomerResponseModel customerResponseModel = customerService.addCustomer(new CustomerRequestModel("John","Doe","asq@gmail","address","city","state","country","zip",new ArrayList<>()));
        assertNotNull(customerResponseModel);
        assertEquals("1",customerResponseModel.getCustomerId());
        assertEquals("John",customerResponseModel.getFirstName());
        assertEquals("Doe",customerResponseModel.getLastName());
        assertEquals("asq@gmail",customerResponseModel.getEmailAddress());
        assertEquals("address",customerResponseModel.getStreetAddress());
        assertEquals("city",customerResponseModel.getCity());
        assertEquals("state",customerResponseModel.getProvince());
        assertEquals("country",customerResponseModel.getCountry());
        assertEquals("zip",customerResponseModel.getPostalCode());
    }




}