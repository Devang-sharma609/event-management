package dev.hiwa.iticket.domain.entities;

import dev.hiwa.iticket.domain.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private String venue;

    @Column(name = "sales_starts_at")
    private LocalDateTime salesStartsAt;

    @Column(name = "sales_ends_at")
    private LocalDateTime salesEndsAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ManyToMany(mappedBy = "attendingEvents")
    private List<User> attendees = new ArrayList<>();

    @ManyToMany(mappedBy = "staffingEvents")
    private List<User> staff = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> ticketTypes = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId()) && Objects.equals(getName(),
                                                                        event.getName()
        ) && Objects.equals(getStartsAt(), event.getStartsAt()) && Objects.equals(
                getEndsAt(),
                event.getEndsAt()
        ) && Objects.equals(getVenue(), event.getVenue()) && Objects.equals(
                getSalesStartsAt(),
                event.getSalesStartsAt()
        ) && Objects.equals(getSalesEndsAt(),
                            event.getSalesEndsAt()
        ) && getEventStatus() == event.getEventStatus() && Objects.equals(getCreatedAt(),
                                                                          event.getCreatedAt()
        ) && Objects.equals(getUpdatedAt(), event.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                            getName(),
                            getStartsAt(),
                            getEndsAt(),
                            getVenue(),
                            getSalesStartsAt(),
                            getSalesEndsAt(),
                            getEventStatus(),
                            getCreatedAt(),
                            getUpdatedAt()
        );
    }
}
