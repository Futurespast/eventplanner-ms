package com.eventplanner.customers.dataacesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUpDb(){
        customerRepository.deleteAll();}

    @Test
    public void whenCustomerExist_ReturnCustomerByCustomerId(){
        //arrange
        Customer customer1 = new Customer("John", "Doe", "Email", new Address("1 street", "City", "Province", "Canada", "aaaaa"),new ArrayList<>());
        customerRepository.save(customer1);
        //act
        Customer saved = customerRepository.findByCustomerIdentifier_CustomerId(customer1.getCustomerIdentifier().getCustomerId());
        //assert
        assertNotNull(customer1);
        assertEquals(saved.getCustomerIdentifier(), customer1.getCustomerIdentifier());
        assertEquals(saved.getFirstName(), customer1.getFirstName());
        assertEquals(saved.getLastName(), customer1.getLastName());
        assertEquals(saved.getAddress(), customer1.getAddress());
        assertEquals(saved.getPhoneNumbers(),customer1.getPhoneNumbers());
    }
    @Test
    public void whenCustomerDoesNotExist_ReturnNull(){
        //arrange
        String customerId = UUID.randomUUID().toString();
        //act
        Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);
        //assert
        assertNull(customer);
    }

}