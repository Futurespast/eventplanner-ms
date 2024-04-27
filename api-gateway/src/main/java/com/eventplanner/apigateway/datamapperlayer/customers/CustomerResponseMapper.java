package com.eventplanner.apigateway.datamapperlayer.customers;


import com.eventplanner.apigateway.presentationlayer.customers.CustomerController;
import com.eventplanner.apigateway.presentationlayer.customers.CustomerResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerResponseMapper {
    CustomerResponseModel responseModelToResponseModel(CustomerResponseModel customerResponseModel);
    List<CustomerResponseModel> responseListToresponseList(List<CustomerResponseModel> customerResponseModels);

    @AfterMapping
    default void addLinks(@MappingTarget CustomerResponseModel customerResponseModel){
        Link selfLink = linkTo(methodOn(CustomerController.class).getCustomerById(customerResponseModel.getCustomerId())).withSelfRel();
        customerResponseModel.add(selfLink);

       Link allLink = linkTo(methodOn(CustomerController.class)
               .getAllCustomer())
        .withRel("all customers");
       customerResponseModel.add(allLink);
    }
}
