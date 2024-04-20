package com.eventplanner.events.dataacesslayer;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event,String> {
    Event getEventByEventIdentifier_EventId(String eventId);
}
