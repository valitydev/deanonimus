package dev.vality.deanonimus.handler;

import dev.vality.damsel.deanonimus.DeanonimusSrv;
import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.converter.SearchHitConverter;
import dev.vality.deanonimus.converter.SearchHitShopConverter;
import dev.vality.deanonimus.converter.SearchHitWalletConverter;
import dev.vality.deanonimus.db.SearchDao;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeanonimusServiceHandler implements DeanonimusSrv.Iface {

    private final SearchHitConverter searchHitConverter;
    private final SearchHitShopConverter searchHitShopConverter;
    private final SearchHitWalletConverter searchHitWalletConverter;
    private final SearchDao searchDao;

    @Override
    public List<SearchHit> searchParty(String text) {
        log.info("Incoming request for party with text: {}", text);
        SearchResponse<Party> searchHits = searchDao.searchParty(text);
        var parties = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.info("Found for party search parties: {}", parties);
        var foundSearchHits = searchHitConverter.convert(searchHits);
        log.info("Found party: {}", foundSearchHits);
        return foundSearchHits;
    }

    @Override
    public List<SearchShopHit> searchShopText(String text) {
        log.info("Incoming request for shop with text: {}", text);
        SearchResponse<Party> searchHits = searchDao.searchParty(text);
        var parties = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.info("Found for shop search parties: {}", parties);
        var foundSearchHits = searchHitShopConverter.convert(searchHits);
        log.info("Found shop: {}", foundSearchHits);
        return foundSearchHits;
    }

    @Override
    public List<SearchWalletHit> searchWalletText(String text) {
        log.info("Incoming request for wallets with text: {}", text);
        SearchResponse<Party> searchHits = searchDao.searchParty(text);
        var parties = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.info("Found for wallet search parties: {}", parties);
        var foundSearchHits = searchHitWalletConverter.convert(searchHits);
        log.info("Found wallet: {}", foundSearchHits);
        return foundSearchHits;
    }
}
