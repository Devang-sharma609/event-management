package com.devang.abhyudaya.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.devang.abhyudaya.domains.dtos.request.PurchaseTicketRequest;
import com.devang.abhyudaya.domains.dtos.response.GetAllTickets_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.GetTicketForUser_TicketResponse;
import com.devang.abhyudaya.domains.dtos.response.PurchaseTicketResponse;
import com.devang.abhyudaya.services.QrCodeService;
import com.devang.abhyudaya.services.TicketService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final QrCodeService qrCodeService;

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseTicketResponse> purchaseTicket(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PurchaseTicketRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(ticketService.purchaseTicket(userId, request));
    }

    @GetMapping
    public ResponseEntity<Page<GetAllTickets_TicketResponse>> getAllUserTickets(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "25") Integer size
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(ticketService.getAllUserTickets(userId, page, size));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<GetTicketForUser_TicketResponse> getTicket(
            @AuthenticationPrincipal Jwt jwt, @PathVariable("id") UUID id
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(ticketService.getTicketForUser(userId, id));
    }

    @GetMapping("/{ticketId}/qr-code")
    public ResponseEntity<byte[]> getTicketQrCode(
            @AuthenticationPrincipal Jwt jwt, @PathVariable("id") UUID id
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        byte[] qrCode = qrCodeService.getQrCodeImageForTicket(userId, id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCode.length);

        return ResponseEntity.ok().headers(headers).body(qrCode);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> cancelTicket(
            @AuthenticationPrincipal Jwt jwt, @PathVariable("id") UUID id
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ticketService.cancelTicket(userId, id);
        return ResponseEntity.noContent().build();
    }
}
