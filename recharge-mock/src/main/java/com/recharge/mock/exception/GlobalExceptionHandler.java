package com.recharge.mock.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ErrorInfo<String> errorResult(HttpServletRequest req) {
        ErrorInfo<String> r = new ErrorInfo<String>();
        r.setMessage("参数异常");
        r.setCode(ErrorInfo.ERROR);
        r.setUrl(req.getRequestURL().toString());
        return r;
    }

    @ExceptionHandler(value = MockException.class)
    public ErrorInfo<String> jsonErrorHandler(HttpServletRequest req, MockException e) throws Exception {
        ErrorInfo<String> r = new ErrorInfo<String>();
        r.setMessage(e.getMessage());
        r.setCode(ErrorInfo.ERROR);
        r.setData("Some Data");
        r.setUrl(req.getRequestURL().toString());
        return r;
    }

}
