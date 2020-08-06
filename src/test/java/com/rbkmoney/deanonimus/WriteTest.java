package com.rbkmoney.deanonimus;


import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.domain.Blocking;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.awaitility.Awaitility.await;

public class WriteTest extends IntegrationTestBase {

    @Autowired
    private PartyRepository partyRepository;

    @Test
    public void onPartyCreatedElasticHaveIt() {

        sendMessage(TestData.createSinkEvent(TestData.SOURCE_ID_ONE, TestData.partyCreated()));

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent() && partyOptional.get().getId().equals(TestData.SOURCE_ID_ONE)
        );

    }

    @Test
    public void onPartyBlockingPartyChanges() {
        sendMessages(TestData.createSinkEvents(TestData.SOURCE_ID_ONE,
                List.of(
                        TestData.partyCreated(),
                        TestData.partyBlocked())
        ));

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent()
                        && partyOptional.get().getId().equals(TestData.SOURCE_ID_ONE)
                        && partyOptional.get().getBlocking().equals(Blocking.blocked)
        );

    }

}
