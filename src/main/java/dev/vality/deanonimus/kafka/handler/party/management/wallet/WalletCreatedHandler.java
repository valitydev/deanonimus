package dev.vality.deanonimus.kafka.handler.party.management.wallet;

import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.WalletEffectUnit;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.domain.Wallet;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletCreatedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetWalletEffect() && claimEffect.getWalletEffect().getEffect().isSetCreated()) {
                handleEvent(event, changeId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, ClaimEffect e) {
        long sequenceId = event.getEventId();
        WalletEffectUnit walletEffect = e.getWalletEffect();
        dev.vality.damsel.domain.Wallet walletCreated = walletEffect.getEffect().getCreated();
        String walletId = walletEffect.getId();
        String partyId = event.getSourceId();
        log.info("Start wallet created handling, sequenceId={}, partyId={}, walletId={}, changeId={}",
                sequenceId, partyId, walletId, changeId);

        Party party = openSearchService.findPartyById(partyId);

        Wallet wallet = fillWalletInfo(walletCreated, walletId);

        party.addWallet(wallet);

        openSearchService.updateParty(party);

        log.info("End wallet created handling, sequenceId={}, partyId={}, walletId={}, changeId={}",
                sequenceId, partyId, walletId, changeId);
    }

    private Wallet fillWalletInfo(dev.vality.damsel.domain.Wallet walletCreated, String walletId) {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setName(walletCreated.getName());
        return wallet;
    }


}
