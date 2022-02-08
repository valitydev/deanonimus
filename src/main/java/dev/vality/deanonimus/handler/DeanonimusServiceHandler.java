package dev.vality.deanonimus.handler;

import dev.vality.damsel.deanonimus.DeanonimusSrv;
import dev.vality.damsel.deanonimus.SearchHit;
import dev.vality.deanonimus.converter.SearchHitConverter;
import dev.vality.deanonimus.db.SearchDao;
import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeanonimusServiceHandler implements DeanonimusSrv.Iface {

    private final SearchHitConverter searchHitConverter;
    private final SearchDao searchDao;

    @Override
    public List<SearchHit> searchParty(String text) throws TException {
        log.info("Incoming request with text: {}", text);
        SearchHits<Party> searchHits = searchDao.searchParty(text);
        log.info("Found: {}", searchHits);
        return searchHitConverter.convert(searchHits);
    }
}
