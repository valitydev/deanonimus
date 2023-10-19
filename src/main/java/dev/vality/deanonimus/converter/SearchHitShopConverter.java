package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class SearchHitShopConverter {

    private final ShopListConverter converter;

    public List<SearchShopHit> convert(SearchHits<Party> searchHits) {
        List<SearchShopHit> hits = new ArrayList<>();
        for (SearchHit<Party> searchHit : searchHits) {
            hits.addAll(converter.convert(searchHit.getContent().getShops()).values()
                    .stream()
                    .map(shop -> new SearchShopHit(searchHit.getScore(), shop))
                    .collect(toList()));
        }
        return hits;
    }
}
