package com.eventplanner.customers.businesslayer;


import com.eventplanner.customers.dataacesslayer.Address;
import com.eventplanner.customers.dataacesslayer.Customer;
import com.eventplanner.customers.dataacesslayer.CustomerIdentifier;
import com.eventplanner.customers.dataacesslayer.CustomerRepository;
import com.eventplanner.customers.datamapperlayer.CustomerRequestMapper;
import com.eventplanner.customers.datamapperlayer.CustomerResponseMapper;
import com.eventplanner.customers.presentationlayer.CustomerRequestModel;
import com.eventplanner.customers.presentationlayer.CustomerResponseModel;
import com.eventplanner.customers.utils.InvalidCustomerNameException;
import com.eventplanner.customers.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerResponseMapper customerResponseMapper;
    private final CustomerRequestMapper customerRequestMapper;


    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerResponseMapper customerResponseMapper, CustomerRequestMapper customerRequestMapper) {
        this.customerRepository = customerRepository;
        this.customerResponseMapper = customerResponseMapper;
        this.customerRequestMapper = customerRequestMapper;
    }

    @Override
    public List<CustomerResponseModel> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerResponseMapper.entityListToResponseModelList(customers);
    }

    @Override
    public CustomerResponseModel getCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);

        if (customer == null) {
            throw new NotFoundException("Unknown customerId: " + customerId);
        }
        return customerResponseMapper.entityToResponseModel(customer);
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        Address address = new Address(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
                customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());

        if (!isValidName(customerRequestModel.getFirstName()) || !isValidName(customerRequestModel.getLastName())) {
            throw new InvalidCustomerNameException(customerRequestModel.getFirstName()+" "+customerRequestModel.getLastName());
        }

        Customer customer = customerRequestMapper.requestModelToEntity(customerRequestModel, new CustomerIdentifier(), address);

        customer.setAddress(address);
        return customerResponseMapper.entityToResponseModel(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, String customerId) {

        Customer existingCustomer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);


        if (existingCustomer == null) {
            throw new NotFoundException("Unknown customerId: " + customerId);
        }

        if (!isValidName(customerRequestModel.getFirstName()) || !isValidName(customerRequestModel.getLastName())) {
            throw new InvalidCustomerNameException(customerRequestModel.getFirstName()+" "+customerRequestModel.getLastName());
        }

        Address address = new Address(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
                customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());
        Customer updatedCustomer = customerRequestMapper.requestModelToEntity(customerRequestModel,
                existingCustomer.getCustomerIdentifier(), address);
        updatedCustomer.setId(existingCustomer.getId());

        Customer response = customerRepository.save(updatedCustomer);
        return customerResponseMapper.entityToResponseModel(response);
    }

    @Override
    public void removeCustomer(String customerId) {
        Customer existingCustomer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);

        if (existingCustomer == null) {
            throw new NotFoundException("Unknown customerId: " + customerId);
        }

        customerRepository.delete(existingCustomer);
    }
    private boolean isValidName(String name) {
        String nameRegex = "^[a-zA-Z\\s'-]+$";
        return name != null && !name.trim().isEmpty() && name.matches(nameRegex);
    }
}
