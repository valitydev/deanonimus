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
import org.springframework.stereotype.Component;

import java.util.List;

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
        log.info("Found party: {}", searchHits);
        return searchHitConverter.convert(searchHits);
    }

    @Override
    public List<SearchShopHit> searchShopText(String text) {
        log.info("Incoming request for shop with text: {}", text);
        SearchResponse<Party> searchHits = searchDao.searchParty(text);
        log.info("Found shop: {}", searchHits);
        return searchHitShopConverter.convert(searchHits);
    }

    @Override
    public List<SearchWalletHit> searchWalletText(String text) {
        log.info("Incoming request for wallets with text: {}", text);
        SearchResponse<Party> searchHits = searchDao.searchParty(text);
        log.info("Found wallet: {}", searchHits);
        return searchHitWalletConverter.convert(searchHits);
    }
}
