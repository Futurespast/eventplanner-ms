package com.eventplanner.events.domainclientlayer.Customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class CustomerModel {
    String customerId;
    String firstName;
    String lastName;
}
