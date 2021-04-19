package com.recharge.mock.exception;

import lombok.Data;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
public class ErrorInfo<T> {
    public static final Integer OK = 0;
    public static final Integer ERROR = 500;

    private Integer code;
    private String message;
    private String url;
    private T data;
}
