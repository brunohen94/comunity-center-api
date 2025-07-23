package com.phoebus.communitycentersapi.repository;

import com.phoebus.communitycentersapi.model.Negociation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NegociationRepository extends MongoRepository<Negociation, String> {
}