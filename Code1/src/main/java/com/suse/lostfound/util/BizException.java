package com.suse.lostfound.util;

/** 可直接展示给用户的业务校验错误。 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
