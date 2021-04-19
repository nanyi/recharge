package com.recharge.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage implements Serializable {
    private static final long serialVersionUID = 7660001522510404706L;

    private boolean flag;
    private Integer code;
    private String message;
    private Object data;
    private String url;

    public ResponseMessage(boolean flag, Integer code, String message, Object data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseMessage ok(Object data) {
        return new ResponseMessage(true, StatusCode.OK, "success", data);
    }

    public static ResponseMessage error(Object data) {
        return new ResponseMessage(true, StatusCode.ERROR, "fail", data);
    }
}
