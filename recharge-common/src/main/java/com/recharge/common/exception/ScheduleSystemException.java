package com.recharge.common.exception;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class ScheduleSystemException extends RuntimeException {
    private static final long serialVersionUID = 8286547329168629668L;

    public ScheduleSystemException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public ScheduleSystemException(final Throwable cause) {
        super(cause);
    }
}
