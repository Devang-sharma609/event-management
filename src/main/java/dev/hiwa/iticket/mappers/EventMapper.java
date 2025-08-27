package dev.hiwa.iticket.mappers;

import dev.hiwa.iticket.domain.dto.request.CreateEventRequest;
import dev.hiwa.iticket.domain.dto.request.UpdateEventRequest;
import dev.hiwa.iticket.domain.dto.response.CreateEventResponse;
import dev.hiwa.iticket.domain.dto.response.EventResponse;
import dev.hiwa.iticket.domain.dto.response.GetPublishedEventDetailsResponse;
import dev.hiwa.iticket.domain.dto.response.UpdateEventResponse;
import dev.hiwa.iticket.domain.entities.Event;
import org.mapstruct.*;

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
