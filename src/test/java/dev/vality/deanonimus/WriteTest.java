package dev.vality.deanonimus;


import dev.vality.deanonimus.db.PartyRepository;
import dev.vality.deanonimus.domain.Blocking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static dev.vality.deanonimus.PartyFlowGenerator.*;
import static org.awaitility.Awaitility.await;

public class WriteTest extends AbstractIntegrationTest {

    @Autowired
    private PartyRepository partyRepository;

    @Test
    void onPartyCreatedElasticHaveIt() throws IOException {

        sendMessages(generatePartyContractorFlow(TestData.SOURCE_ID_ONE));

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent() && partyOptional.get().getId().equals(TestData.SOURCE_ID_ONE)
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

        await().until(() -> partyRepository.findById(TestData.SOURCE_ID_ONE),
                partyOptional -> partyOptional.isPresent()
                        && partyOptional.get().getId().equals(TestData.SOURCE_ID_ONE)
                        && partyOptional.get().getBlocking().equals(Blocking.blocked)
        );

    }

}
