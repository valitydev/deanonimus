package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class SearchHitConverter {

    private final PartyConverter partyConverter;

    public List<SearchHit> convert(SearchResponse<Party> searchHits) {
        return searchHits.hits().hits().stream()
                .map(this::convertSearchHit)
                .collect(toList());
    }

    private SearchHit convertSearchHit(Hit<Party> partySearchHit) {
        return new SearchHit(partySearchHit.score(), partyConverter.convert(partySearchHit.source()));
    }

}
