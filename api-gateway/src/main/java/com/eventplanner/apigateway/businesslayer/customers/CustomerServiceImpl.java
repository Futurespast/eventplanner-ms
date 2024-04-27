package com.eventplanner.apigateway.businesslayer.customers;


import com.eventplanner.apigateway.datamapperlayer.customers.CustomerResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.customers.CustomersServiceClient;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

private  final CustomersServiceClient customersServiceClient;
private final CustomerResponseMapper customerResponseMapper;

    public CustomerServiceImpl(CustomersServiceClient customersServiceClient, CustomerResponseMapper customerResponseMapper) {
        this.customersServiceClient = customersServiceClient;
        this.customerResponseMapper = customerResponseMapper;
    }

    @Override
    public List<CustomerResponseModel> getAllCustomers() {
        return customerResponseMapper.responseListToresponseList(customersServiceClient.getAllCustomers());
    }

    @Override
    public CustomerResponseModel getCustomerById(String customerId) {
        return customerResponseMapper.responseModelToResponseModel(customersServiceClient.getCustomerByCustomerId(customerId));
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        return customerResponseMapper.responseModelToResponseModel(customersServiceClient.addCustomer(customerRequestModel));
    }

    @Override
    public void updateCustomer(CustomerRequestModel customerRequestModel, String customerId) {
        customersServiceClient.updateCustomer(customerRequestModel,customerId);
    }

    @Override
    public void deleteCustomer(String customerId) {
        customersServiceClient.deleteCustomer(customerId);
    }
}
