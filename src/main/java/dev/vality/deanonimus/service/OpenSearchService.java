package dev.vality.deanonimus.service;

import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.wallet.Identity;
import dev.vality.deanonimus.domain.wallet.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.springframework.stereotype.Service;

import static dev.vality.deanonimus.constant.OpenSearchConstants.*;

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

    @SneakyThrows
    public Wallet createWallet(Wallet wallet) {
        openSearchClient.index(new IndexRequest.Builder<Wallet>()
                .index(WALLET_INDEX)
                .id(wallet.getId())
                .document(wallet)
                .build());
        return wallet;
    }

    @SneakyThrows
    public Wallet findWalletById(String walletId) {
        return openSearchClient.get(new GetRequest.Builder()
                        .index(WALLET_INDEX)
                        .id(walletId)
                        .build(),
                Wallet.class).source();
    }

    @SneakyThrows
    public void updateWallet(Wallet wallet) {
        openSearchClient.update(
                new UpdateRequest.Builder<Wallet, Wallet>()
                        .index(WALLET_INDEX)
                        .id(wallet.getId())
                        .doc(wallet)
                        .build(),
                Wallet.class);
    }


    @SneakyThrows
    public Identity createIdentity(Identity identity) {
        openSearchClient.index(new IndexRequest.Builder<Identity>()
                .index(IDENTITY_INDEX)
                .id(identity.getId())
                .document(identity)
                .build());
        return identity;
    }

    @SneakyThrows
    public Identity findIdentityById(String identityId) {
        return openSearchClient.get(new GetRequest.Builder()
                        .index(IDENTITY_INDEX)
                        .id(identityId)
                        .build(),
                Identity.class).source();
    }
}
