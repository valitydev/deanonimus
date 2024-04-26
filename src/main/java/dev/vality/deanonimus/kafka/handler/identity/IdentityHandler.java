package dev.vality.deanonimus.kafka.handler.identity;

import dev.vality.deanonimus.kafka.handler.Handler;
import dev.vality.fistful.identity.TimestampedChange;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface IdentityHandler extends Handler<TimestampedChange, MachineEvent> {
}
