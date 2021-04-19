package com.recharge.common.exception;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class TaskNotExistException extends RuntimeException {
    private static final long serialVersionUID = 5904436222007997750L;

    public TaskNotExistException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public TaskNotExistException(final Throwable cause) {
        super(cause);
    }
}
