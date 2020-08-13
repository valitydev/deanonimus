package com.rbkmoney.deanonimus.util;

import com.rbkmoney.damsel.deanonimus.*;

public class EnumUtils {

    public static Blocking convertBlocking(com.rbkmoney.deanonimus.domain.Blocking blocking) {
        switch (blocking) {
            case blocked:
                return Blocking.blocked(new Blocked());
            case unblocked:
                return Blocking.unblocked(new Unblocked());
            default:
                throw new RuntimeException("No such blocking state");
        }
    }

    public static Suspension convertSuspension(com.rbkmoney.deanonimus.domain.Suspension suspension) {
        switch (suspension) {
            case active:
                return Suspension.active(new Active());
            case suspended:
                return Suspension.suspended(new Suspended());
            default:
                throw new RuntimeException("No such suspension state");
        }
    }
}
