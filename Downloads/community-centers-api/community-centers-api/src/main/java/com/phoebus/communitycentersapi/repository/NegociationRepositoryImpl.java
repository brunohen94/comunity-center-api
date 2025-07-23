package com.phoebus.communitycentersapi.repository;

import com.phoebus.communitycentersapi.model.Negociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class NegociationRepositoryImpl implements NegociationRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Negociation> findHistoric(String communityCenterName, LocalDateTime negociationDate) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("originCenterName").is(communityCenterName),
                Criteria.where("destinationCenterName").is(communityCenterName)
        ));

        if (negociationDate != null) {
            Instant instant = negociationDate.atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneId.of("UTC"))
                    .toInstant();
            Date date = Date.from(instant);
            query.addCriteria(Criteria.where("timestamp").gte(date));
        }

        return mongoTemplate.find(query, Negociation.class);
    }
}