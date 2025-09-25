package com.devang.abhyudaya.domains.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTicketResponse {

    @JsonProperty("ticket_id")
    private UUID id;

    private PurchaseTicket_TicketTypeResponse ticketType;

    @JsonProperty("qr_code")
    private QrCodeResponse qrCode;
}
