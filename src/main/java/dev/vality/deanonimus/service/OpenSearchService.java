package dev.vality.deanonimus.service;

import dev.vality.deanonimus.domain.Party;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.springframework.stereotype.Service;

import static dev.vality.deanonimus.constant.OpenSearchConstants.PARTY_INDEX;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchService {

    private final OpenSearchClient openSearchClient;

    @SneakyThrows
    public Party findPartyById(String partyId) {
        return openSearchClient.get(new GetRequest.Builder()
                        .index(PARTY_INDEX)
                        .id(partyId)
                        .build(),
                Party.class).source();
    }

    @SneakyThrows
    public void updateParty(Party party) {
        openSearchClient.update(
                new UpdateRequest.Builder<Party, Party>()
                        .index(PARTY_INDEX)
                        .id(party.getId())
                        .doc(party)
                        .build(),
                Party.class);
    }

    @SneakyThrows
    public Party createParty(Party party) {
        openSearchClient.index(new IndexRequest.Builder<Party>()
                .index(PARTY_INDEX)
                .id(party.getId())
                .document(party)
                .build());
        return party;
    }
}
