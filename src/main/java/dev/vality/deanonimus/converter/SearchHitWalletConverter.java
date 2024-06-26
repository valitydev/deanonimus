package dev.vality.deanonimus.converter;

import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.domain.wallet.Wallet;
import dev.vality.deanonimus.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

@Component
@RequiredArgsConstructor
public class SearchHitWalletConverter {

    private final WalletListConverter walletListConverter;
    private final PartyConverter partyConverter;
    private final OpenSearchService openSearchService;

    public List<SearchWalletHit> convert(SearchResponse<Wallet> searchHits, String text) {
        var hits = new ArrayList<SearchWalletHit>();
        for (var searchHit : searchHits.hits().hits()) {
            hits.addAll(walletListConverter.convertWallets(List.of(searchHit.source())).values()
                    .stream()
                    .filter(wallet -> containsIgnoreCase(wallet.getId(), text)
                            || containsIgnoreCase(wallet.getName(), text))
                    .map(wallet -> new SearchWalletHit(
                            searchHit.score(),
                            wallet,
                            partyConverter.convert(openSearchService.findPartyById(searchHit.source().getPartyId()))))
                    .toList());
        }
        return hits;
    }
}
