package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class SearchHitWalletConverter {

    private final WalletListConverter walletListConverter;
    private final PartyConverter partyConverter;


    public List<SearchWalletHit> convert(SearchResponse<Party> searchHits) {
        List<SearchWalletHit> hits = new ArrayList<>();
        for (Hit<Party> searchHit : searchHits.hits().hits()) {
            hits.addAll(walletListConverter.convert(searchHit.source().getWallets()).values()
                    .stream()
                    .map(wallet -> new SearchWalletHit(
                            searchHit.score(),
                            wallet,
                            partyConverter.convert(searchHit.source())))
                    .collect(toList()));
        }
        return hits;
    }
}
