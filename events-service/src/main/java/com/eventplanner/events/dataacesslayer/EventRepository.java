package com.eventplanner.events.dataacesslayer;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event,String> {
    List<Event> getAllByCustomerModel_CustomerId(String customerId);
    Event getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(String customerId, String eventId);
}
