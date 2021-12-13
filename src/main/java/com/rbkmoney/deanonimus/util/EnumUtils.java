package com.rbkmoney.deanonimus.util;

import com.rbkmoney.damsel.deanonimus.*;

public class EnumUtils {

    public static Blocking convertBlocking(com.rbkmoney.deanonimus.domain.Blocking blocking) {
        return switch (blocking) {
            case blocked -> Blocking.blocked(new Blocked());
            case unblocked -> Blocking.unblocked(new Unblocked());
            default -> throw new IllegalArgumentException("No such blocking state " + blocking);
        };
    }

    public static Suspension convertSuspension(com.rbkmoney.deanonimus.domain.Suspension suspension) {
        return switch (suspension) {
            case active -> Suspension.active(new Active());
            case suspended -> Suspension.suspended(new Suspended());
            default -> throw new IllegalArgumentException("No such suspension state " + suspension);
        };
    }
}
