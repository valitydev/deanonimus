package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface SearchDao {

    SearchHits<Party> searchParty(String text);

}
