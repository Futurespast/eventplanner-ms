package com.eventplanner.apigateway.presentationlayer.customers;
import com.eventplanner.apigateway.domainclientlayer.customers.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class CustomerRequestModel {
    String firstName;
    String lastName;
    String emailAddress;
    String streetAddress;
    String city;
    String province;
    String country;
    String postalCode;
    List<PhoneNumber> phoneNumbers;
}
