package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    private UUID id;
    private String value;
    private QrCodeStatus status;

}
