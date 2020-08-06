package com.rbkmoney.deanonimus.db;

import com.rbkmoney.deanonimus.domain.Party;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends ElasticsearchRepository<Party, String> {


}
