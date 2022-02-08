package dev.vality.deanonimus.kafka.handler;

import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.PartyEventData;
import dev.vality.deanonimus.kafka.handler.party.management.PartyManagementHandler;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyManagementHandlerService {

    private final List<PartyManagementHandler> partyManagementHandlers;
    private final MachineEventParser<PartyEventData> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        PartyEventData eventPayload = parser.parse(machineEvent);
        final List<PartyChange> partyChanges = eventPayload.getChanges();
        for (int i = 0; i < partyChanges.size(); i++) {
            PartyChange partyChange = partyChanges.get(i);
            Integer changeId = i;
            partyManagementHandlers.stream()
                    .filter(handler -> handler.accept(partyChange))
                    .forEach(handler -> handler.handle(partyChange, machineEvent, changeId));
        }

    }

}
