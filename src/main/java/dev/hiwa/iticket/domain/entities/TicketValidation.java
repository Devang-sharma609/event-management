package dev.hiwa.iticket.domain.entities;

import dev.hiwa.iticket.domain.enums.TicketValidationMethod;
import dev.hiwa.iticket.domain.enums.TicketValidationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "ticket_validations")
public class TicketValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketValidationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_method", nullable = false)
    private TicketValidationMethod validationMethod;

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

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
        TicketValidation that = (TicketValidation) o;
        return Objects.equals(getId(),
                              that.getId()
        ) && getStatus() == that.getStatus() && getValidationMethod() == that.getValidationMethod() && Objects.equals(getValidatedAt(),
                                                                                                                      that.getValidatedAt()
        ) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(
                getUpdatedAt(),
                that.getUpdatedAt()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                            getStatus(),
                            getValidationMethod(),
                            getValidatedAt(),
                            getCreatedAt(),
                            getUpdatedAt()
        );
    }
}
