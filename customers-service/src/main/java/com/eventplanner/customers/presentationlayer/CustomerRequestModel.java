package com.eventplanner.customers.presentationlayer;


import com.eventplanner.customers.dataacesslayer.PhoneNumber;
import lombok.*;

import java.util.List;

@Data
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
