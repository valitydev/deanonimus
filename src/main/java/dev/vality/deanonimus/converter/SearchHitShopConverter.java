package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

@Component
@RequiredArgsConstructor
public class SearchHitShopConverter {

    private final ShopListConverter shopListConverter;
    private final PartyConverter partyConverter;

    public List<SearchShopHit> convert(SearchResponse<Party> searchHits, String text) {
        var hits = new ArrayList<SearchShopHit>();
        for (var searchHit : searchHits.hits().hits()) {
            hits.addAll(shopListConverter.convert(searchHit.source().getShops()).values()
                    .stream()
                    .filter(shop -> containsIgnoreCase(shop.getId(), text)
                            || containsIgnoreCase(shop.getLocation().getUrl(), text)
                            || containsIgnoreCase(shop.getDetails().getName(), text))
                    .map(shop -> new SearchShopHit(searchHit.score(), shop, partyConverter.convert(searchHit.source())))
                    .toList());
        }
        return hits;
    }
}
