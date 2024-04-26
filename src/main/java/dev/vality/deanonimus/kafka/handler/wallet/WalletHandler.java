package dev.vality.deanonimus.kafka.handler.wallet;

import dev.vality.deanonimus.kafka.handler.Handler;
import dev.vality.fistful.wallet.TimestampedChange;
import dev.vality.machinegun.eventsink.MachineEvent;

public interface WalletHandler extends Handler<TimestampedChange, MachineEvent> {
}
