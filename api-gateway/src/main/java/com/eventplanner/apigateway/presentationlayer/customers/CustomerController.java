package com.eventplanner.apigateway.presentationlayer.customers;


import com.eventplanner.apigateway.businesslayer.customers.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/customers")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponseModel>> getAllCustomer(){
        return ResponseEntity.ok().body(customerService.getAllCustomers());
    }
    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseModel> getCustomerById(@PathVariable String customerId){
        return ResponseEntity.ok().body(customerService.getCustomerById(customerId));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseModel> addCustomer(@RequestBody CustomerRequestModel customerRequestModel){
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addCustomer(customerRequestModel));
    }

    @PutMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseModel> updateCustomer(@RequestBody CustomerRequestModel customerRequestModel, @PathVariable String customerId){
        customerService.updateCustomer(customerRequestModel,customerId);
        CustomerResponseModel customerResponseModel = customerService.getCustomerById(customerId);
        return ResponseEntity.ok().body(customerResponseModel);
    }

    @DeleteMapping(value = "/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId){
        customerService.deleteCustomer(customerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
