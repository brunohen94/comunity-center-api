package com.phoebus.communitycentersapi.dtos;

import com.phoebus.communitycentersapi.model.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CreateNegociationFormDTO {

    @NotNull(message = "Origin center name is required")
    @NotBlank(message = "Origin center name cannot be empty")
    private String originCenterName;

    @NotNull(message = "Destination center name is required")
    @NotBlank(message = "Destination center name cannot be empty")
    private String destinationCenterName;

    @NotNull(message = "Origin resources is required")
    @Valid
    private List<NegociationResourcesFormDTO> originResources;

    @NotNull(message = "Destination resources is required")
    @Valid
    private List<NegociationResourcesFormDTO> destinationResources;

    public Negociation transformToObject() {
        List<NegociationResources> originResourcesList = this.originResources.stream()
                .map(resourceFormDTO -> new NegociationResources(resourceFormDTO.getName(), resourceFormDTO.getQuantity()))
                .collect(Collectors.toList());

        List<NegociationResources> destinationResourcesList = this.destinationResources.stream()
                .map(resourceFormDTO -> new NegociationResources(resourceFormDTO.getName(), resourceFormDTO.getQuantity()))
                .collect(Collectors.toList());

        return new Negociation(
                null,
                this.getOriginCenterName(),
                this.getDestinationCenterName(),
                originResourcesList,
                destinationResourcesList,
                LocalDateTime.now()
        );
    }
}