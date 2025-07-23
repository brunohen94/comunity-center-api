package com.phoebus.communitycentersapi.dtos;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;

@Getter
@Setter
public class UpdateOccupancyFormDTO {
    @Min(value = 0, message = "Occupancy must be at least 0")
    private Integer occupancy;
}