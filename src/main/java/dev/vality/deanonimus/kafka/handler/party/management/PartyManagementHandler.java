package dev.vality.deanonimus.kafka.handler.party.management;

import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface PartyManagementHandler extends Handler<PartyChange, MachineEvent> {}
