package com.devang.abhyudaya.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.devang.abhyudaya.domains.dtos.response.*;
import com.devang.abhyudaya.domains.entities.QrCode;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QrCodeMapper {

    QrCodeResponse toQrCodeResponse(QrCode qrCode);

}
