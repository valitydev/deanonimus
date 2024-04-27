package dev.vality.deanonimus;


import dev.vality.deanonimus.domain.Blocking;
import dev.vality.deanonimus.service.OpenSearchService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static dev.vality.deanonimus.PartyFlowGenerator.*;
import static org.awaitility.Awaitility.await;

public class WriteTest extends AbstractIntegrationTest {

    @Autowired
    OpenSearchService openSearchService;

    @Test
    void onPartyCreatedElasticHaveIt() throws IOException {

        sendPartyMessages(generatePartyContractorFlow(TestData.SOURCE_ID_ONE));
        sleep();
        await().until(() -> openSearchService.findPartyById(TestData.SOURCE_ID_ONE),
                party -> party != null && party.getId().equals(TestData.SOURCE_ID_ONE)
        );

    }

    @Test
    void onPartyCreatedWalletFlowElasticHaveIt() throws IOException {
        var partyId = "partyId";
        sendMessages(TOPIC_IDENTITY, generateIdentityFlow(TestData.SOURCE_ID_ONE, partyId));
        sendMessages(TOPIC_WALLET, generateWalletFlow(TestData.SOURCE_ID_ONE, TestData.SOURCE_ID_ONE));
        sleep();
        await().until(() -> openSearchService.findIdentityById(TestData.SOURCE_ID_ONE),
                val -> val != null && val.getId().equals(TestData.SOURCE_ID_ONE));
        await().until(() -> openSearchService.findWalletById(TestData.SOURCE_ID_ONE),
                val -> val != null && val.getPartyId().equals(partyId));
    }

    @Test
    void onPartyBlockingPartyChanges() {
        sendPartyMessages(
                List.of(
                        buildSinkEvent(buildMessagePartyCreated(0L, TestData.SOURCE_ID_ONE)),
                        buildSinkEvent(buildMessagePartyBlocking(0L, TestData.SOURCE_ID_ONE))
                )
        );
        sleep();
        await().until(() -> openSearchService.findPartyById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional != null
                        && partyOptional.getId().equals(TestData.SOURCE_ID_ONE)
                        && partyOptional.getBlocking().equals(Blocking.blocked)
        );

    }

    @SneakyThrows
    private void sleep() {
        Thread.sleep(5000L);
    }
}
