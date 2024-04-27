package dev.vality.deanonimus.handler;

import dev.vality.damsel.deanonimus.DeanonimusSrv;
import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.damsel.deanonimus.SearchShopHit;
import dev.vality.damsel.deanonimus.SearchWalletHit;
import dev.vality.deanonimus.converter.SearchHitConverter;
import dev.vality.deanonimus.converter.SearchHitShopConverter;
import dev.vality.deanonimus.converter.SearchHitWalletConverter;
import dev.vality.deanonimus.db.SearchByShopDao;
import dev.vality.deanonimus.db.SearchDaoImpl;
import dev.vality.deanonimus.db.WalletDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeanonimusServiceHandler implements DeanonimusSrv.Iface {

    private final SearchHitConverter searchHitConverter;
    private final SearchHitShopConverter searchHitShopConverter;
    private final SearchHitWalletConverter searchHitWalletConverter;
    private final SearchDaoImpl searchDao;
    private final SearchByShopDao searchByShopDao;
    private final WalletDao walletDao;

    @Override
    public List<SearchHit> searchParty(String text) {
        log.info("Incoming request for party with text: {}", text);
        var searchHits = searchDao.searchParty(text);
        var parties = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.debug("Found for party search parties: {}", parties);
        log.info("Found for party search parties: {}", parties.size());
        var foundSearchHits = searchHitConverter.convert(searchHits);
        log.debug("Found party: {}", foundSearchHits);
        log.info("Found party: {}", foundSearchHits.size());
        return foundSearchHits;
    }

    @Override
    public List<SearchShopHit> searchShopText(String text) {
        log.info("Incoming request for shop with text: {}", text);
        var searchHits = searchByShopDao.searchParty(text);
        var parties = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.debug("Found for shop search parties: {}", parties);
        log.info("Found for shop search parties: {}", parties.size());
        var foundSearchHits = searchHitShopConverter.convert(searchHits, text);
        log.debug("Found shop: {}", foundSearchHits);
        log.info("Found shop: {}", foundSearchHits.size());
        return foundSearchHits;
    }

    @Override
    public List<SearchWalletHit> searchWalletText(String text) {
        log.info("Incoming request for wallets with text: {}", text);
        var searchHits = walletDao.searchWallet(text);
        var wallets = searchHits.hits().hits().stream().map(Hit::source).collect(toList());
        log.debug("Found for wallet search wallets: {}", wallets);
        log.info("Found for wallet search wallets: {}", wallets.size());
        var foundSearchHits = searchHitWalletConverter.convert(searchHits, text);
        log.debug("Found wallet: {}", foundSearchHits);
        log.info("Found wallet: {}", foundSearchHits.size());
        return foundSearchHits;
    }
}
