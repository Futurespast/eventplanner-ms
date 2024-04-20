package com.eventplanner.events.businesslayer;

import com.example.eventplanningws.Common.CustomerIdentifier;
import com.example.eventplanningws.Common.ParticipantIdentifier;
import com.example.eventplanningws.Common.VenueIdentifier;
import com.example.eventplanningws.CustomerSubDomain.DataLayer.Customer;
import com.example.eventplanningws.CustomerSubDomain.DataLayer.CustomerRepository;
import com.example.eventplanningws.EventSubDomain.DataLayer.Event;
import com.example.eventplanningws.EventSubDomain.DataLayer.EventIdentifier;
import com.example.eventplanningws.EventSubDomain.DataLayer.EventRepository;
import com.example.eventplanningws.EventSubDomain.DataMapperLayer.EventRequestMapper;
import com.example.eventplanningws.EventSubDomain.DataMapperLayer.EventResponseMapper;
import com.example.eventplanningws.EventSubDomain.PresentationLayer.EventRequestModel;
import com.example.eventplanningws.EventSubDomain.PresentationLayer.EventResponseModel;
import com.example.eventplanningws.ParticipantSubDomain.DataLayer.Participant;
import com.example.eventplanningws.ParticipantSubDomain.DataLayer.ParticipantRepository;
import com.example.eventplanningws.VenueSubDomain.DataLayer.Venue;
import com.example.eventplanningws.VenueSubDomain.DataLayer.VenueRepository;
import com.example.eventplanningws.utils.InvalidInputException;
import com.example.eventplanningws.utils.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final CustomerRepository customerRepository;
    private final VenueRepository venueRepository;
    private final ParticipantRepository participantRepository;
    private final EventResponseMapper eventResponseMapper;
    private final EventRequestMapper eventRequestMapper;

    public EventServiceImpl(EventRepository eventRepository, CustomerRepository customerRepository, VenueRepository venueRepository, ParticipantRepository participantRepository, EventResponseMapper eventResponseMapper, EventRequestMapper eventRequestMapper) {
        this.eventRepository = eventRepository;
        this.customerRepository = customerRepository;
        this.venueRepository = venueRepository;
        this.participantRepository = participantRepository;
        this.eventResponseMapper = eventResponseMapper;
        this.eventRequestMapper = eventRequestMapper;
    }

    @Override
    public List<EventResponseModel> getEvents() {
        List<EventResponseModel> eventResponseModelList = new ArrayList<>();
        List<Event> events = eventRepository.findAll();
        events.forEach(event -> {
            Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(event.getCustomerIdentifier().getCustomerId());
            if(customer == null){
                throw new InvalidInputException("Customerid provided is invalid"+ event.getCustomerIdentifier().getCustomerId());
            }
            Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(event.getVenueIdentifier().getVenueId());
            if(venue == null){
                throw new InvalidInputException("venueId provided is invalid"+ event.getVenueIdentifier().getVenueId());
            }
            EventResponseModel eventResponseModel = eventResponseMapper.entityToEventResponseModel(event,customer,venue);
            List<Participant> participants = participantRepository.findAllByEventIdentifier_EventId(event.getEventIdentifier().getEventId());
            List<String> participantsId = new ArrayList<>();
            participants.forEach(participant -> {
                participantsId.add(participant.getParticipantIdentifier().getParticipantId());
            });
            eventResponseModel.setParticipantsId(participantsId);
            eventResponseModelList.add(eventResponseModel);
        });

        return eventResponseModelList;
    }

    @Override
    public EventResponseModel getEventById(String eventId) {
       Event event = eventRepository.getEventByEventIdentifier_EventId(eventId);
       if(event == null){
           throw  new NotFoundException("eventId provided doesn't exist"+eventId);
       }
       Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(event.getCustomerIdentifier().getCustomerId());
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ event.getCustomerIdentifier().getCustomerId());
        }
       Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(event.getVenueIdentifier().getVenueId());
        if(venue == null){
            throw new InvalidInputException("venueId provided is invalid"+ event.getVenueIdentifier().getVenueId());
        }
       EventResponseModel eventResponseModel = eventResponseMapper.entityToEventResponseModel(event,customer,venue);
        List<Participant> participants = participantRepository.findAllByEventIdentifier_EventId(event.getEventIdentifier().getEventId());
        List<String> participantsId = new ArrayList<>();
        participants.forEach(participant -> {
            participantsId.add(participant.getParticipantIdentifier().getParticipantId());
        });
        eventResponseModel.setParticipantsId(participantsId);
        return eventResponseModel;
    }

    @Override
    public EventResponseModel addEvent(EventRequestModel eventRequestModel) {
       Event event = eventRequestMapper.eventRequestModelToEntity(eventRequestModel,new EventIdentifier(), new VenueIdentifier(eventRequestModel.getVenueId()), new CustomerIdentifier(eventRequestModel.getCustomerId()));
        Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(event.getCustomerIdentifier().getCustomerId());
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ event.getCustomerIdentifier().getCustomerId());
        }
        Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(event.getVenueIdentifier().getVenueId());
        if(venue == null){
            throw new InvalidInputException("venueId provided is invalid"+ event.getVenueIdentifier().getVenueId());
        }
        List<LocalDate> dates = venue.getAvailableDates();
        if(!dates.contains(event.getEventDate().getStartDate()) || !dates.contains(event.getEventDate().getEndDate())){
            throw new InvalidInputException("Venue is not available for the event's date");
        }
        List<ParticipantIdentifier> participantsId = new ArrayList<>();
        event.setParticipantIdentifiers(participantsId);
        Event savedEvent = eventRepository.save(event);
        EventResponseModel eventResponseModel = eventResponseMapper.entityToEventResponseModel(savedEvent, customer, venue);
        List<String>participants = new ArrayList<>();
        eventResponseModel.setParticipantsId(participants);
        return eventResponseModel;
    }

    @Override
    public EventResponseModel updateEvent(EventRequestModel eventRequestModel, String eventId) {
        Event oldEvent = eventRepository.getEventByEventIdentifier_EventId(eventId);
        if(oldEvent == null){
            throw  new NotFoundException("eventId provided doesn't exist"+eventId);
        }
        Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(oldEvent.getCustomerIdentifier().getCustomerId());
        if(customer == null){
            throw new InvalidInputException("Customerid provided is invalid"+ oldEvent.getCustomerIdentifier().getCustomerId());
        }
        Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(oldEvent.getVenueIdentifier().getVenueId());
        if(venue == null){
            throw new InvalidInputException("venueId provided is invalid"+ oldEvent.getVenueIdentifier().getVenueId());
        }
        Event updatedEvent = eventRequestMapper.eventRequestModelToEntity(eventRequestModel, new EventIdentifier(eventId), new VenueIdentifier(eventRequestModel.getVenueId()), new CustomerIdentifier(eventRequestModel.getCustomerId()));
        List<LocalDate> dates = venue.getAvailableDates();

        if(!dates.contains(updatedEvent.getEventDate().getStartDate()) || !dates.contains(updatedEvent.getEventDate().getEndDate())){
            throw new InvalidInputException("Venue is not available for the event's date");
        }

        List<Participant> participants = participantRepository.findAllByEventIdentifier_EventId(eventId);
        List<ParticipantIdentifier> participantsIdentifier = new ArrayList<>();
        participants.forEach(participant -> {
            participantsIdentifier.add(participant.getParticipantIdentifier());
        });
        updatedEvent.setParticipantIdentifiers(participantsIdentifier);
        updatedEvent.setId(oldEvent.getId());
        Event savedEvent = eventRepository.save(updatedEvent);
        EventResponseModel eventResponseModel = eventResponseMapper.entityToEventResponseModel(savedEvent, customer, venue);
        List<String> participantsId = new ArrayList<>();
        participants.forEach(participant -> {
            participantsId.add(participant.getParticipantIdentifier().getParticipantId());
        });
        eventResponseModel.setParticipantsId(participantsId);
        return eventResponseModel;
    }

    @Override
    public void deleteEvent(String eventId) {
        Event event = eventRepository.getEventByEventIdentifier_EventId(eventId);
        if(event == null){
            throw  new NotFoundException("eventId provided doesn't exist"+eventId);
        }
        List<Participant> participants = participantRepository.findAllByEventIdentifier_EventId(eventId);
        participants.forEach(participant -> {
            participantRepository.delete(participant);
        });
        eventRepository.delete(event);
    }
}
