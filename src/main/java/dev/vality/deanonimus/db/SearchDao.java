package dev.vality.deanonimus.db;

import dev.vality.deanonimus.domain.Party;
import org.opensearch.client.opensearch.core.SearchResponse;

public interface SearchDao {

    SearchResponse<Party> searchParty(String text);

}
