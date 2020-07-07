package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class UnknownException extends RetCodeException
{
    public UnknownException(String message) {
        super(RetCode.UNKNOWN_ERROR, message);
    }

    public UnknownException(String message, Throwable cause) {
        super(RetCode.UNKNOWN_ERROR, message, cause);
    }
}
