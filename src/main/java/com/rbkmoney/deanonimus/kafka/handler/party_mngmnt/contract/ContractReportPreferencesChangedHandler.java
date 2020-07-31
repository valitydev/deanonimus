package com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.contract;

import com.rbkmoney.damsel.domain.ReportPreferences;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.deanonimus.db.PartyRepository;
import com.rbkmoney.deanonimus.db.exception.ContractNotFoundException;
import com.rbkmoney.deanonimus.db.exception.PartyNotFoundException;
import com.rbkmoney.deanonimus.domain.Contract;
import com.rbkmoney.deanonimus.domain.Party;
import com.rbkmoney.deanonimus.kafka.handler.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.deanonimus.util.ContractUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractReportPreferencesChangedHandler extends AbstractClaimChangedHandler {

    private final PartyRepository partyRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (ClaimEffect claimEffect : claimEffects) {
            if (claimEffect.isSetContractEffect() && claimEffect.getContractEffect().getEffect().isSetReportPreferencesChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffect);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        ReportPreferences reportPreferencesChanged = contractEffectUnit.getEffect().getReportPreferencesChanged();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract report preferences changed handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);

        Party party = partyRepository.findById(partyId).orElseThrow(PartyNotFoundException::new);

        Contract contract = party.getContractById(contractId).orElseThrow(ContractNotFoundException::new);

        if (reportPreferencesChanged != null && reportPreferencesChanged.isSetServiceAcceptanceActPreferences()) {
            ContractUtil.fillReportPreferences(contract, reportPreferencesChanged.getServiceAcceptanceActPreferences());
        } else {
            ContractUtil.setNullReportPreferences(contract);
        }

        partyRepository.save(party);

        log.info("End contract report preferences changed handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
    }
}
