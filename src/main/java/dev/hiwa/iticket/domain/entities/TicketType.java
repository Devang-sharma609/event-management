package dev.hiwa.iticket.domain.entities;

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
@Table(name = "ticket_types")
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "total_available")
    private Integer totalAvailable;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "ticketType")
    private List<Ticket> tickets = new ArrayList<>();

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
        TicketType that = (TicketType) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(),
                                                                       that.getName()
        ) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(
                getPrice(),
                that.getPrice()
        ) && Objects.equals(getTotalAvailable(),
                            that.getTotalAvailable()
        ) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(
                getUpdatedAt(),
                that.getUpdatedAt()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                            getName(),
                            getDescription(),
                            getPrice(),
                            getTotalAvailable(),
                            getCreatedAt(),
                            getUpdatedAt()
        );
    }
}

