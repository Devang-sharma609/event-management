package com.devang.abhyudaya.mappers;

import org.mapstruct.*;

import com.devang.abhyudaya.domains.dtos.request.CreateEventRequest;
import com.devang.abhyudaya.domains.dtos.request.UpdateEventRequest;
import com.devang.abhyudaya.domains.dtos.response.CreateEventResponse;
import com.devang.abhyudaya.domains.dtos.response.EventResponse;
import com.devang.abhyudaya.domains.dtos.response.GetPublishedEventDetailsResponse;
import com.devang.abhyudaya.domains.dtos.response.UpdateEventResponse;
import com.devang.abhyudaya.domains.entities.Event;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TicketTypeMapper.class}
)
public interface EventMapper {


    Event toEntity(CreateEventRequest createEventRequest);

    Event toEntity(UpdateEventRequest updateEventRequest);

    CreateEventResponse toCreateEventResponse(Event event);

    UpdateEventResponse toUpdateEventResponse(Event event);

    EventResponse toEventResponse(Event event);

    GetPublishedEventDetailsResponse toGetPublishedEventResponse(Event event);

    void update(@MappingTarget Event event, UpdateEventRequest request);

    @AfterMapping
    default void afterMappingToEntity(@MappingTarget Event event) {
        event.getTicketTypes().forEach(tt -> tt.setEvent(event));
    }

}
