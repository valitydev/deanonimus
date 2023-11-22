package dev.vality.deanonimus;


import dev.vality.deanonimus.domain.Blocking;
import dev.vality.deanonimus.service.OpenSearchService;
import org.junit.jupiter.api.Test;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static dev.vality.deanonimus.PartyFlowGenerator.*;
import static org.awaitility.Awaitility.await;

public class WriteTest extends AbstractIntegrationTest {

    @Autowired
    OpenSearchService openSearchService;

    @Test
    void onPartyCreatedElasticHaveIt() throws IOException {

        sendMessages(generatePartyContractorFlow(TestData.SOURCE_ID_ONE));

        await().until(() -> openSearchService.findPartyById(TestData.SOURCE_ID_ONE),
                party -> party != null && party.getId().equals(TestData.SOURCE_ID_ONE)
        );

    }

    @Test
    void onPartyBlockingPartyChanges() {
        sendMessages(
                List.of(
                        buildSinkEvent(buildMessagePartyCreated(0L, TestData.SOURCE_ID_ONE)),
                        buildSinkEvent(buildMessagePartyBlocking(0L, TestData.SOURCE_ID_ONE))
                )
        );

        await().until(() -> openSearchService.findPartyById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional != null
                        && partyOptional.getId().equals(TestData.SOURCE_ID_ONE)
                        && partyOptional.getBlocking().equals(Blocking.blocked)
        );

    }

}
