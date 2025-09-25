package com.devang.abhyudaya.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devang.abhyudaya.services.TicketValidationService;
import com.devang.abhyudaya.domains.dtos.request.TicketValidationRequest;
import com.devang.abhyudaya.domains.dtos.response.TicketValidationResponse;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ticket-validations")
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;

    // STAFF
    @PostMapping
    public ResponseEntity<TicketValidationResponse> validateTicket(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TicketValidationRequest request
    ) {
        UUID staffId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ticketValidationService.validateTicket(request, staffId));
    }
}