package dev.hiwa.iticket.mappers;

import dev.hiwa.iticket.domain.dto.response.*;
import dev.hiwa.iticket.domain.entities.QrCode;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QrCodeMapper {

    QrCodeResponse toQrCodeResponse(QrCode qrCode);

}
