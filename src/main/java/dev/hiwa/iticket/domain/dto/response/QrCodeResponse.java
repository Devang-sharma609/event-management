package dev.hiwa.iticket.domain.dto.response;

import dev.hiwa.iticket.domain.enums.QrCodeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    @JsonProperty("qr_code_id")
    private UUID id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("status")
    private QrCodeStatus status;

}
