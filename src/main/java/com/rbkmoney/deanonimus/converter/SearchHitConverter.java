package com.rbkmoney.deanonimus.converter;

import com.rbkmoney.damsel.deanonimus.SearchHit;
import com.rbkmoney.deanonimus.domain.Party;
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
