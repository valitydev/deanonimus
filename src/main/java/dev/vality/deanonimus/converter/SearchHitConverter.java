package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class SearchHitConverter {

    private final PartyConverter partyConverter;

    public List<SearchHit> convert(SearchHits<Party> searchHits) {
        return searchHits.stream()
                .map(this::convertSearchHit)
                .collect(toList());
    }

    private SearchHit convertSearchHit(org.springframework.data.elasticsearch.core.SearchHit<Party> partySearchHit) {
        return new SearchHit(partySearchHit.getScore(), partyConverter.convert(partySearchHit.getContent()));
    }
}
