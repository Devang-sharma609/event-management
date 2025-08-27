package dev.hiwa.iticket.controller;

import dev.hiwa.iticket.domain.dto.request.TicketValidationRequest;
import dev.hiwa.iticket.domain.dto.response.TicketValidationResponse;
import dev.hiwa.iticket.service.TicketValidationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ticket-validations")
@SecurityRequirement(name = "Keycloak")
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;

    // STAFF
    @PostMapping
    public ResponseEntity<TicketValidationResponse> validateTicket(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TicketValidationRequest request
    ) {
        return ResponseEntity.ok(ticketValidationService.validateTicket(request));
    }
}