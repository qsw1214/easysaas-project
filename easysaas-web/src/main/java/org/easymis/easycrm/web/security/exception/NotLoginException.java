package org.easymis.easycrm.web.security.exception;

public class NotLoginException extends RuntimeException {
    public NotLoginException(String msg) {
        super(msg);
    }

    public NotLoginException() {
    }

}
