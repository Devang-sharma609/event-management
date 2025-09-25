package com.devang.abhyudaya.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devang.abhyudaya.domains.dtos.request.CreateEventRequest;
import com.devang.abhyudaya.domains.dtos.request.UpdateEventRequest;
import com.devang.abhyudaya.domains.dtos.request.UpdateTicketTypeRequest;
import com.devang.abhyudaya.domains.dtos.response.CreateEventResponse;
import com.devang.abhyudaya.domains.dtos.response.EventResponse;
import com.devang.abhyudaya.domains.dtos.response.GetPublishedEventDetailsResponse;
import com.devang.abhyudaya.domains.dtos.response.UpdateEventResponse;
import com.devang.abhyudaya.domains.entities.Event;
import com.devang.abhyudaya.domains.entities.TicketType;
import com.devang.abhyudaya.domains.entities.User;
import com.devang.abhyudaya.domains.enums.EventStatus;
import com.devang.abhyudaya.exceptions.ResourceNotFoundException;
import com.devang.abhyudaya.mappers.EventMapper;
import com.devang.abhyudaya.mappers.TicketTypeMapper;
import com.devang.abhyudaya.repositories.EventRepository;
import com.devang.abhyudaya.repositories.TicketTypeRepository;
import com.devang.abhyudaya.repositories.UserRepository;

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

                Page<Event> eventsPage = eventRepository.findAllWithTicketTypesByOrganizer_Id(organizerId, pageRequest);

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
                                                "No such Event with id '%s' exists for Organizer with id '%s'"
                                                                .formatted(eventId,
                                                                                organizerId), HttpStatus.NOT_FOUND));

                return eventMapper.toEventResponse(event);

        }

        @Transactional
        public UpdateEventResponse updateEvent(UUID organizerId, UUID eventId, UpdateEventRequest request) {
                // Step 1: Load the existing event
                Event eventToUpdate = eventRepository
                                .findByIdAndOrganizer_Id(eventId, organizerId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No such Event with id '%s' exists for Organizer with id '%s'"
                                                                .formatted(eventId, organizerId), HttpStatus.NOT_FOUND));

                // Step 2: Extract TicketType IDs
                Set<UUID> incomingTicketTypeIds = request
                                .getTicketTypes()
                                .stream()
                                .map(UpdateTicketTypeRequest::getId)
                                .collect(Collectors.toSet());

                Set<TicketType> existingTicketTypes = ticketTypeRepository.findAllByIdIn(incomingTicketTypeIds);

                if (existingTicketTypes.size() != incomingTicketTypeIds.size()) {
                        Set<UUID> foundIds = existingTicketTypes.stream().map(TicketType::getId)
                                        .collect(Collectors.toSet());
                        Set<UUID> missingIds = new HashSet<>(incomingTicketTypeIds);
                        missingIds.removeAll(foundIds);

                        throw new ResourceNotFoundException("TicketType(s) not found for ID(s): " + missingIds, HttpStatus.NOT_FOUND);
                }

                // Step 3: Map TicketType entities by ID
                Map<UUID, TicketType> existingById = existingTicketTypes.stream()
                                .collect(Collectors.toMap(TicketType::getId, tt -> tt));

                // Step 4: Update scalar fields of Event
                eventMapper.update(eventToUpdate, request);

                // Step 5: Update ticket types and build updated list
                List<TicketType> updatedTicketTypes = new ArrayList<>();

                for (UpdateTicketTypeRequest ticketReq : request.getTicketTypes()) {
                        UUID ticketTypeId = ticketReq.getId();
                        TicketType ticketType = existingById.get(ticketTypeId);

                        if (!ticketType.getEvent().getId().equals(eventId)) {
                                throw new ResourceNotFoundException(
                                                "TicketType with ID '%s' does not belong to Event with ID '%s'"
                                                                .formatted(
                                                                                ticketTypeId,
                                                                                eventId),HttpStatus.CONFLICT);
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

                Page<Event> eventsPage = eventRepository.findAllWithTicketTypesByEventStatus(EventStatus.PUBLISHED,
                                pageRequest);

                return eventsPage.map(eventMapper::toEventResponse);
        }

        @Transactional(readOnly = true)
        public Page<EventResponse> searchPublishedEvents(String query, Integer page, Integer size) {
                var pageRequest = PageRequest.of(page, size);

                Page<Event> searchedEvents = eventRepository.searchPublishedEventsByNameOrVenue(query, pageRequest);

                return searchedEvents.map(eventMapper::toEventResponse);
        }

        @Transactional(readOnly = true)
        public GetPublishedEventDetailsResponse getPublishedEvent(UUID eventId) {
                Event event = eventRepository
                                .findWithTicketTypesByIdAndEventStatus(eventId, EventStatus.PUBLISHED)
                                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId.toString()));

                return eventMapper.toGetPublishedEventResponse(event);
        }

        @Transactional
        public void assignStaffToEvent(UUID eventId, List<UUID> userIds) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found", HttpStatus.NOT_FOUND));
                for (UUID userId : userIds) {
                        User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Event not found", HttpStatus.NOT_FOUND));
                        if (!event.getStaff().contains(user)) {
                                event.getStaff().add(user);
                                user.getStaffingEvents().add(event); // bidirectional
                        }
                }
                eventRepository.save(event);
        }

        @Transactional
        public List<UUID> getAssignedStaff(UUID eventId) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found", HttpStatus.NOT_FOUND));
                return event.getStaff().stream()
                                .map(User::getId)
                                .collect(Collectors.toList());
        }
}