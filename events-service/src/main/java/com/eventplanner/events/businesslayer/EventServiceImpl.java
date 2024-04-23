package com.eventplanner.events.businesslayer;


import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.dataacesslayer.EventIdentifier;
import com.eventplanner.events.dataacesslayer.EventRepository;
import com.eventplanner.events.datamapperlayer.EventRequestMapper;
import com.eventplanner.events.datamapperlayer.EventResponseMapper;
import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Customer.CustomersServiceClient;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantsServiceClient;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import com.eventplanner.events.domainclientlayer.Venue.VenuesServiceClient;
import com.eventplanner.events.presentationlayer.EventRequestModel;
import com.eventplanner.events.presentationlayer.EventResponseModel;
import com.eventplanner.events.utils.InvalidEventDateException;
import com.eventplanner.events.utils.InvalidInputException;
import com.eventplanner.events.utils.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;

    private final CustomersServiceClient customersServiceClient;

    private final VenuesServiceClient venuesServiceClient;

    private final ParticipantsServiceClient participantsServiceClient;

    private final EventResponseMapper eventResponseMapper;
    private final EventRequestMapper eventRequestMapper;

    public EventServiceImpl(EventRepository eventRepository, CustomersServiceClient customersServiceClient, VenuesServiceClient venuesServiceClient, ParticipantsServiceClient participantsServiceClient, EventResponseMapper eventResponseMapper, EventRequestMapper eventRequestMapper) {
        this.eventRepository = eventRepository;
        this.customersServiceClient = customersServiceClient;
        this.venuesServiceClient = venuesServiceClient;
        this.participantsServiceClient = participantsServiceClient;
        this.eventResponseMapper = eventResponseMapper;
        this.eventRequestMapper = eventRequestMapper;
    }


    @Override
    public List<EventResponseModel> getEvents(String customerId) {
        CustomerModel customer =customersServiceClient.getCustomerByCustomerId(customerId);
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ customerId);
        }

        List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerId);

       return eventResponseMapper.entityToEventResponseList(events);
    }

    @Override
    public EventResponseModel getEventById(String customerId, String eventId) {
        CustomerModel customer =customersServiceClient.getCustomerByCustomerId(customerId);
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ customerId);
        }

        Event event = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(customerId,eventId);
        if(event == null){
            throw new InvalidInputException("eventId provided is invalid"+ customerId);
        }
        return eventResponseMapper.entityToEventResponseModel(event);
    }

    @Override
    public EventResponseModel addEvent(String customerId, EventRequestModel eventRequestModel) {
        CustomerModel customer =customersServiceClient.getCustomerByCustomerId(customerId);
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ customerId);
        }
        VenueModel venueModel = venuesServiceClient.getVenueByVenueId(eventRequestModel.getVenueId());
        if(venueModel == null){
            throw new InvalidInputException("venue id provided is "+ eventRequestModel.getVenueId());
        }
        if(!venueModel.getAvailableDates().contains(eventRequestModel.getEventDate().getStartDate())||!venueModel.getAvailableDates().contains(eventRequestModel.getEventDate().getEndDate())){
            throw new InvalidEventDateException("Event date is not available with the venue");
        }
        venuesServiceClient.updateVenueDates(venueModel.getVenueId(), eventRequestModel.getEventDate().getStartDate(),eventRequestModel.getEventDate().getEndDate());
        List<ParticipantModel> participantModels = new ArrayList<>();
        List<String> participantIds = eventRequestModel.getParticipantIds();
        if (participantIds != null) {
            for (String id : participantIds) {
                ParticipantModel participant = participantsServiceClient.getParticipantById(id);
                if (participant == null) {
                    throw new InvalidInputException("Participant ID provided is invalid: " + id);
                }
                participantModels.add(participant);
            }
        }

       Event event = eventRequestMapper.eventRequestModelToEntity(eventRequestModel,new EventIdentifier(),venueModel,customer);
        event.setParticipantModels(participantModels);
        Event saved =eventRepository.save(event);
        return eventResponseMapper.entityToEventResponseModel(saved);
    }

    @Override
    public EventResponseModel updateEvent(String customerId, String eventId, EventRequestModel eventRequestModel) {
        CustomerModel customer =customersServiceClient.getCustomerByCustomerId(customerId);
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ customerId);
        }
        VenueModel venueModel = venuesServiceClient.getVenueByVenueId(eventRequestModel.getVenueId());
        if(venueModel == null){
            throw new InvalidInputException("venue id provided is "+ eventRequestModel.getVenueId());
        }
        if(!venueModel.getAvailableDates().contains(eventRequestModel.getEventDate().getStartDate())||!venueModel.getAvailableDates().contains(eventRequestModel.getEventDate().getEndDate())){
            throw new InvalidEventDateException("Event date is not available with the venue");
        }
        List<ParticipantModel> participantModels = new ArrayList<>();
        List<String> participantIds = eventRequestModel.getParticipantIds();
        if (participantIds != null) {
            for (String id : participantIds) {
                ParticipantModel participant = participantsServiceClient.getParticipantById(id);
                if (participant == null) {
                    throw new InvalidInputException("Participant ID provided is invalid: " + id);
                }
                participantModels.add(participant);
            }
        }
        Event foundEvent = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(customerId,eventId);
        if(foundEvent == null){
            throw new InvalidInputException("eventId provided is invalid"+ customerId);
        }
        Event event = eventRequestMapper.eventRequestModelToEntity(eventRequestModel, foundEvent.getEventIdentifier(),venueModel,customer);
        event.setParticipantModels(participantModels);
        Event save = eventRepository.save(event);
        return eventResponseMapper.entityToEventResponseModel(save);
    }

    @Override
    public void deleteEvent(String customerId, String eventId) {
        CustomerModel customer =customersServiceClient.getCustomerByCustomerId(customerId);
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ customerId);
        }
        Event event = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(customerId,eventId);
        if(event == null){
            throw new InvalidInputException("eventId provided is invalid"+ customerId);
        }
        eventRepository.delete(event);
    }
}
