package com.eventplanner.customers.dataacesslayer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="customers")
@Data
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //private identifier

    @Embedded
    private CustomerIdentifier customerIdentifier; //public identifier

    private String firstName;
    private String lastName;
    private String emailAddress;

    @Embedded
    private Address address;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "customer_phonenumbers", joinColumns = @JoinColumn(name="customer_id"))
    private List<PhoneNumber> phoneNumbers;

    public Customer(@NotNull String firstName, @NotNull String lastName, @NotNull String emailAddress, @NotNull Address address,
                    @NotNull List<PhoneNumber> phoneNumberList) {
        this.customerIdentifier = new CustomerIdentifier();
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.address = address;
        this.phoneNumbers = phoneNumberList;
    }
}
