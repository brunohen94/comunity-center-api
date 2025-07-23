package com.phoebus.communitycentersapi.repository;

import com.phoebus.communitycentersapi.model.Negociation;

import java.time.LocalDateTime;
import java.util.List;

public interface NegociationRepositoryCustom {
    List<Negociation> findHistoric(String communityCenterName, LocalDateTime negociationDate);
}