package dev.hiwa.iticket.domain.entities;

import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "qr_codes")
public class QrCode {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QrCodeStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QrCode qrCode = (QrCode) o;
        return Objects.equals(
                getId(),
                qrCode.getId()
        ) && getStatus() == qrCode.getStatus() && Objects.equals(getValue(), qrCode.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus(), getValue());
    }
}
