package com.rbkmoney.deanonimus;

import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.SearchDao;
import com.rbkmoney.deanonimus.domain.Party;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;

import static org.awaitility.Awaitility.await;

public class ReadTest extends IntegrationTestBase {

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    SearchDao searchDao;

    @Test
    public void searchByShopUrl() {
//        final IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Party.class);
//        indexOperations.create();
//        indexOperations.putMapping(indexOperations.createMapping(Party.class));
        givenParty();
        givenShop();

        final SearchHits<Party> localhost = searchDao.searchParty("localhost.ru");

        System.out.println();
    }

    private void givenShop() {
        sendMessage(TestData.createSinkEvent(TestData.SOURCE_ID_ONE, TestData.shopCreated()));

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent()
                        && partyOptional.get().getShops() != null
                        && partyOptional.get().getShops().size() > 0
        );
    }

    private void givenParty() {
        sendMessage(TestData.createSinkEvent(TestData.SOURCE_ID_ONE, TestData.partyCreated()));

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent() && partyOptional.get().getId().equals(TestData.SOURCE_ID_ONE)
        );
    }

}
