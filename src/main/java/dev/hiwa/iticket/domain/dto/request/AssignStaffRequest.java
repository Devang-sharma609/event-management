package dev.hiwa.iticket.domain.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignStaffRequest {
    private List<UUID> userIds;
}