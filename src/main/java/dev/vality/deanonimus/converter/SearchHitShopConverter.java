package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.damsel.deanonimus.Shop;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

@Component
@RequiredArgsConstructor
public class SearchHitShopConverter {

    private final ShopListConverter shopListConverter;
    private final PartyConverter partyConverter;

    public List<SearchShopHit> convert(SearchResponse<Party> searchHits, String text) {
        var hits = new ArrayList<SearchShopHit>();
        for (var searchHit : searchHits.hits().hits()) {
            var shops = searchHit.source().getShops();
            if (isContainCommonFields(searchHit.source(), text)) {
                hits.addAll(shopListConverter.convert(shops).values()
                        .stream()
                        .map(shop ->
                                new SearchShopHit(searchHit.score(), shop, partyConverter.convert(searchHit.source())))
                        .toList());
            } else {
                hits.addAll(shopListConverter.convert(shops).values()
                        .stream()
                        .filter(shop -> filterByShopFields(shop, text))
                        .map(shop ->
                                new SearchShopHit(searchHit.score(), shop, partyConverter.convert(searchHit.source())))
                        .toList());
            }
        }
        return hits;
    }

    private boolean filterByShopFields(Shop shop, String text) {
        return containsIgnoreCase(shop.getId(), text)
                || containsIgnoreCase(shop.getLocation().getUrl(), text)
                || containsIgnoreCase(shop.getDetails().getName(), text)
                || containsIgnoreCase(shop.getDetails().getDescription(), text)
                || containsIgnoreCase(shop.getContractId(), text);
    }

    private boolean isContainCommonFields(Party party, String text) {
        var contracts = Optional.ofNullable(party.getContracts());
        var contractors = Optional.ofNullable(party.getContractors());
        return containsIgnoreCase(party.getId(), text)
                || containsIgnoreCase(party.getEmail(), text)
                || contracts.map(list -> list.stream().anyMatch(e -> containsIgnoreCase(e.getId(), text))).orElse(false)
                || contractors.map(list ->
                list.stream().anyMatch(e -> containsIgnoreCase(e.getId(), text))).orElse(false);
    }
}
