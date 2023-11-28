package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchHitWalletConverter {

    private final WalletListConverter walletListConverter;
    private final PartyConverter partyConverter;


    public List<SearchWalletHit> convert(SearchResponse<Party> searchHits, String text) {
        List<SearchWalletHit> hits = new ArrayList<>();
        for (Hit<Party> searchHit : searchHits.hits().hits()) {
            hits.addAll(walletListConverter.convert(searchHit.source().getWallets()).values()
                    .stream()
                    .filter(wallet -> wallet.getId().contains(text)
                            || wallet.getName().contains(text))
                    .map(wallet -> new SearchWalletHit(
                            searchHit.score(),
                            wallet,
                            partyConverter.convert(searchHit.source())))
                    .toList());
        }

        return hits;
    }
}
