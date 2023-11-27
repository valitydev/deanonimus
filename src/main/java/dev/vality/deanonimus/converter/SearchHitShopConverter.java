package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchHitShopConverter {

    private final ShopListConverter shopListConverter;
    private final PartyConverter partyConverter;


    public List<SearchShopHit> convert(SearchResponse<Party> searchHits) {
        List<SearchShopHit> hits = new ArrayList<>();
        for (Hit<Party> searchHit : searchHits.hits().hits()) {
            hits.addAll(shopListConverter.convert(searchHit.source().getShops()).values()
                    .stream()
                    .map(shop -> new SearchShopHit(searchHit.score(), shop, partyConverter.convert(searchHit.source())))
                    .toList());
        }

        return hits;
    }
}
