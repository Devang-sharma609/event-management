package dev.hiwa.iticket.repository;

import dev.hiwa.iticket.domain.entities.Event;
import dev.hiwa.iticket.domain.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @EntityGraph(attributePaths = {"ticketTypes"})
    Page<Event> findAllWithTicketTypesByOrganizer_Id(UUID organizerId, Pageable pageable);

    @EntityGraph(attributePaths = {"ticketTypes"})
    Optional<Event> findWithTicketTypesByIdAndOrganizer_Id(UUID id, UUID organizerId);

    Optional<Event> findByIdAndOrganizer_Id(UUID id, UUID organizerId);

    @EntityGraph(attributePaths = {"ticketTypes"})
    Page<Event> findAllWithTicketTypesByEventStatus(EventStatus eventStatus, Pageable pageable);


    @Query("""
             SELECT e FROM Event e WHERE e.eventStatus = "PUBLISHED" AND
             (LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
             LOWER(e.venue) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Event> searchPublishedEventsByNameOrVenue(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"ticketTypes"})
    Optional<Event> findWithTicketTypesByIdAndEventStatus(UUID id, EventStatus status);

}