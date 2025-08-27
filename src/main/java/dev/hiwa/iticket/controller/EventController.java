package dev.hiwa.iticket.controller;

import dev.hiwa.iticket.domain.dto.request.CreateEventRequest;
import dev.hiwa.iticket.domain.dto.request.UpdateEventRequest;
import dev.hiwa.iticket.domain.dto.response.CreateEventResponse;
import dev.hiwa.iticket.domain.dto.response.EventResponse;
import dev.hiwa.iticket.domain.dto.response.GetPublishedEventDetailsResponse;
import dev.hiwa.iticket.domain.dto.response.UpdateEventResponse;
import dev.hiwa.iticket.service.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
@SecurityRequirement(name = "Keycloak")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponse> createEvent(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateEventRequest request
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        var createdEvent = eventService.createEvent(userId, request);

        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "25") Integer size
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        var eventsPage = eventService.getAllEventsForOrganizer(userId, page, size);

        return ResponseEntity.ok(eventsPage);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(
            @AuthenticationPrincipal Jwt jwt, @PathVariable(name = "eventId") UUID eventId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(eventService.getEvent(userId, eventId));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<UpdateEventResponse> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("eventId") UUID eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        var response = eventService.updateEvent(userId, eventId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal Jwt jwt, @PathVariable("eventId") UUID eventId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        eventService.deleteEvent(userId, eventId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/published")
    public ResponseEntity<Page<EventResponse>> getAllPublishedEvents(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "25") Integer size,
            @RequestParam(name = "q", required = false) String query
    ) {
        var eventsPage = query == null
                ? eventService.getPublishedEvents(page, size)
                : eventService.searchPublishedEvents(query.trim(), page, size);

        return ResponseEntity.ok(eventsPage);
    }

    @GetMapping("/published/{id}")
    public ResponseEntity<GetPublishedEventDetailsResponse> getPublishedEvent(
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(eventService.getPublishedEvent(id));
    }
}


