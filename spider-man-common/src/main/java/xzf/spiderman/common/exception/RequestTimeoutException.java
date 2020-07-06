package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class RequestTimeoutException extends RetCodeException
{
    public RequestTimeoutException(String message) {
        super(RetCode.REQUEST_TIMEOUT_ERROR, message);
    }

    public RequestTimeoutException(String message, Throwable cause) {
        super(RetCode.REQUEST_TIMEOUT_ERROR, message, cause);
    }

    public RequestTimeoutException(String code, String message) {
        super(code, message);
    }

    public RequestTimeoutException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
