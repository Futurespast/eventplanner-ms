package com.eventplanner.apigateway.businesslayer.customers;



import com.eventplanner.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerResponseModel;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseModel> getAllCustomers();
    CustomerResponseModel getCustomerById(String customerId);

    CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel);

    void updateCustomer(CustomerRequestModel customerRequestModel, String customerId);

    void deleteCustomer(String customerId);
}
