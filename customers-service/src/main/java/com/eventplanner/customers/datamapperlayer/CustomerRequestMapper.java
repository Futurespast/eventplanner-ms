package com.eventplanner.customers.datamapperlayer;


import com.eventplanner.customers.dataacesslayer.Address;
import com.eventplanner.customers.dataacesslayer.Customer;
import com.eventplanner.customers.dataacesslayer.CustomerIdentifier;
import com.eventplanner.customers.presentationlayer.CustomerRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    Customer requestModelToEntity(CustomerRequestModel customerRequestModel, CustomerIdentifier customerIdentifier,
                                  Address address);
}
