package com.recharge.common.entity;

import java.io.Serializable;

/**
 * <p>
 * 订单返回对象实体
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public class Result<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -3628649205978144511L;

    /**
     * 错误码
     */
    private Integer code = 500;

    /**
     * 错误信息
     */
    private String msg = null;

    /**
     * 返回结果实体
     */
    private T data = null;


    public Result() {
        this.code = StatusCode.OK;
        this.msg = "操作成功!";
    }

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T extends Serializable> Result<T> error(String msg) {
        return new Result<T>(StatusCode.ERROR, msg, null);
    }

    public static <T extends Serializable> Result<T> error(int code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T extends Serializable> Result<T> success(T data) {
        return new Result<T>(StatusCode.OK, "", data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }

}
