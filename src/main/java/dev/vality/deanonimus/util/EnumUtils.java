package dev.vality.deanonimus.util;

import dev.vality.damsel.deanonimus.*;

public class EnumUtils {

    public static Blocking convertBlocking(dev.vality.deanonimus.domain.Blocking blocking) {
        return switch (blocking) {
            case blocked -> Blocking.blocked(new Blocked());
            case unblocked -> Blocking.unblocked(new Unblocked());
            default -> throw new IllegalArgumentException("No such blocking state " + blocking);
        };
    }

    public static Suspension convertSuspension(dev.vality.deanonimus.domain.Suspension suspension) {
        return switch (suspension) {
            case active -> Suspension.active(new Active());
            case suspended -> Suspension.suspended(new Suspended());
            default -> throw new IllegalArgumentException("No such suspension state " + suspension);
        };
    }
}
