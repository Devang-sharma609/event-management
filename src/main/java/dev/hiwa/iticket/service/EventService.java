package dev.hiwa.iticket.service;

import dev.hiwa.iticket.domain.dto.request.CreateEventRequest;
import dev.hiwa.iticket.domain.dto.request.UpdateEventRequest;
import dev.hiwa.iticket.domain.dto.request.UpdateTicketTypeRequest;
import dev.hiwa.iticket.domain.dto.response.CreateEventResponse;
import dev.hiwa.iticket.domain.dto.response.EventResponse;
import dev.hiwa.iticket.domain.dto.response.GetPublishedEventDetailsResponse;
import dev.hiwa.iticket.domain.dto.response.UpdateEventResponse;
import dev.hiwa.iticket.domain.entities.Event;
import dev.hiwa.iticket.domain.entities.TicketType;
import dev.hiwa.iticket.domain.enums.EventStatus;
import dev.hiwa.iticket.exceptions.ResourceNotFoundException;
import dev.hiwa.iticket.mappers.EventMapper;
import dev.hiwa.iticket.mappers.TicketTypeMapper;
import dev.hiwa.iticket.repository.EventRepository;
import dev.hiwa.iticket.repository.TicketTypeRepository;
import dev.hiwa.iticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final UserRepository userRepository;

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    @Transactional
    public CreateEventResponse createEvent(UUID organizerId, CreateEventRequest request) {
        var organizer = userRepository
                .findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", organizerId.toString()));
        var event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);

        var savedEvent = eventRepository.save(event);
        return eventMapper.toCreateEventResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEventsForOrganizer(UUID organizerId, Integer page, Integer size) {
        var pageRequest = PageRequest.of(page, size);

        userRepository
                .findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", organizerId.toString()));

        Page<Event> eventsPage =
                eventRepository.findAllWithTicketTypesByOrganizer_Id(organizerId, pageRequest);

        return eventsPage.map(eventMapper::toEventResponse);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID organizerId, UUID eventId) {
        userRepository
                .findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", organizerId.toString()));

        var event = eventRepository
                .findByIdAndOrganizer_Id(eventId, organizerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No such Event with id '%s' exists for Organizer with id '%s'".formatted(eventId,
                                                                                                 organizerId
                        )));

        return eventMapper.toEventResponse(event);

    }

    @Transactional
    public UpdateEventResponse updateEvent(UUID organizerId, UUID eventId, UpdateEventRequest request) {
        // Step 1: Load the existing event
        Event eventToUpdate = eventRepository
                .findByIdAndOrganizer_Id(eventId, organizerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No such Event with id '%s' exists for Organizer with id '%s'".formatted(eventId,
                                                                                                 organizerId
                        )));

        // Step 2: Extract TicketType IDs
        Set<UUID> incomingTicketTypeIds = request
                .getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .collect(Collectors.toSet());

        Set<TicketType> existingTicketTypes = ticketTypeRepository.findAllByIdIn(incomingTicketTypeIds);

        if (existingTicketTypes.size() != incomingTicketTypeIds.size()) {
            Set<UUID> foundIds =
                    existingTicketTypes.stream().map(TicketType::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(incomingTicketTypeIds);
            missingIds.removeAll(foundIds);

            throw new ResourceNotFoundException("TicketType(s) not found for ID(s): " + missingIds);
        }

        // Step 3: Map TicketType entities by ID
        Map<UUID, TicketType> existingById =
                existingTicketTypes.stream().collect(Collectors.toMap(TicketType::getId, tt -> tt));

        // Step 4: Update scalar fields of Event
        eventMapper.update(eventToUpdate, request);

        // Step 5: Update ticket types and build updated list
        List<TicketType> updatedTicketTypes = new ArrayList<>();

        for (UpdateTicketTypeRequest ticketReq : request.getTicketTypes()) {
            UUID ticketTypeId = ticketReq.getId();
            TicketType ticketType = existingById.get(ticketTypeId);

            if (!ticketType.getEvent().getId().equals(eventId)) {
                throw new ResourceNotFoundException(
                        "TicketType with ID '%s' does not belong to Event with ID '%s'".formatted(
                                ticketTypeId,
                                eventId
                        ));
            }

            ticketTypeMapper.update(ticketType, ticketReq);
            updatedTicketTypes.add(ticketType);
        }

        // Step 6: Replace ticket types in one go using only managed instances
        eventToUpdate.getTicketTypes().clear();
        eventToUpdate.getTicketTypes().addAll(updatedTicketTypes);

        // Step 7: Save and return the response
        Event savedEvent = eventRepository.save(eventToUpdate);
        return eventMapper.toUpdateEventResponse(savedEvent);
    }

    @Transactional
    public void deleteEvent(UUID organizerId, UUID eventId) {
        getEvent(organizerId, eventId);
        eventRepository.deleteById(eventId);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getPublishedEvents(Integer page, Integer size) {
        var pageRequest = PageRequest.of(page, size);

        Page<Event> eventsPage =
                eventRepository.findAllWithTicketTypesByEventStatus(EventStatus.PUBLISHED, pageRequest);

        return eventsPage.map(eventMapper::toEventResponse);
    }


    @Transactional(readOnly = true)
    public Page<EventResponse> searchPublishedEvents(String query, Integer page, Integer size) {
        var pageRequest = PageRequest.of(page, size);

        Page<Event> searchedEvents =
                eventRepository.searchPublishedEventsByNameOrVenue(query, pageRequest);

        return searchedEvents.map(eventMapper::toEventResponse);
    }

    @Transactional(readOnly = true)
    public GetPublishedEventDetailsResponse getPublishedEvent(UUID eventId) {
        Event event = eventRepository
                .findWithTicketTypesByIdAndEventStatus(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId.toString()));

        return eventMapper.toGetPublishedEventResponse(event);
    }
}
