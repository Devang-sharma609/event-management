package com.devang.abhyudaya.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.devang.abhyudaya.domains.dtos.response.TicketValidationResponse;
import com.devang.abhyudaya.domains.entities.TicketValidation;

@Mapper(
        componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TicketValidationMapper {

    @Mapping(target = "ticketId", source = "ticket.id")
    TicketValidationResponse toTicketValidationResponse(TicketValidation savedValidation);
}
