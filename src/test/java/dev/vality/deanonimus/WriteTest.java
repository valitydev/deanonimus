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

        sendMessages(generatePartyContractorFlow(TestData.SOURCE_ID_ONE));
        sleep();
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
        sleep();
        await().until(() -> openSearchService.findPartyById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional != null
                        && partyOptional.getId().equals(TestData.SOURCE_ID_ONE)
                        && partyOptional.getBlocking().equals(Blocking.blocked)
        );

    }

    @SneakyThrows
    private void sleep() {
        Thread.sleep(5000);
    }

}
