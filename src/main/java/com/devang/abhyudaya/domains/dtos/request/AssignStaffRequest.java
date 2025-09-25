package com.devang.abhyudaya.domains.dtos.request;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignStaffRequest {

    @JsonProperty("userIds")
    private List<UUID> userIds;
}