package dev.vality.deanonimus.kafka.handler.party.management.contract;

import dev.vality.damsel.domain.ReportPreferences;
import dev.vality.damsel.payment_processing.ClaimEffect;
import dev.vality.damsel.payment_processing.ContractEffectUnit;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.deanonimus.db.exception.ContractNotFoundException;
import dev.vality.deanonimus.domain.Contract;
import dev.vality.deanonimus.domain.Party;
import dev.vality.deanonimus.kafka.handler.party.management.AbstractClaimChangedHandler;
import dev.vality.deanonimus.service.OpenSearchService;
import dev.vality.deanonimus.util.ContractUtil;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractReportPreferencesChangedHandler extends AbstractClaimChangedHandler {

    private final OpenSearchService openSearchService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetContractEffect()
                    && claimEffect.getContractEffect().getEffect().isSetReportPreferencesChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        ReportPreferences reportPreferencesChanged = contractEffectUnit.getEffect().getReportPreferencesChanged();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info(
                """
                           Start contract report preferences changed handling,
                           sequenceId={}, partyId={}, contractId={}, changeId={}
                        """,
                sequenceId, partyId, contractId, changeId);

        Party party = openSearchService.findPartyById(partyId);

        Contract contract =
                party.getContractById(contractId).orElseThrow(() -> new ContractNotFoundException(contractId));

        if (reportPreferencesChanged != null && reportPreferencesChanged.isSetServiceAcceptanceActPreferences()) {
            ContractUtil.fillReportPreferences(contract, reportPreferencesChanged.getServiceAcceptanceActPreferences());
        } else {
            ContractUtil.setNullReportPreferences(contract);
        }

        openSearchService.updateParty(party);

        log.info(
                """
                        End contract report preferences changed handling, 
                        sequenceId={}, partyId={}, contractId={}, changeId={}
                        """,
                sequenceId, partyId, contractId, changeId);
    }
}
