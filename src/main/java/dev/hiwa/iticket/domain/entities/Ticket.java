package dev.hiwa.iticket.domain.entities;


import dev.hiwa.iticket.domain.enums.TicketStatus;
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
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatus ticketStatus;

    @ManyToOne
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketValidation> validations = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<QrCode> qrCodes = new ArrayList<>();

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
        Ticket ticket = (Ticket) o;
        return Objects.equals(getId(),
                              ticket.getId()
        ) && getTicketStatus() == ticket.getTicketStatus() && Objects.equals(getCreatedAt(),
                                                                             ticket.getCreatedAt()
        ) && Objects.equals(
                getUpdatedAt(),
                ticket.getUpdatedAt()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTicketStatus(), getCreatedAt(), getUpdatedAt());
    }
}
