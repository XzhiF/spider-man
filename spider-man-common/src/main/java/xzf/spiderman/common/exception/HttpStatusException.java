package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class HttpStatusException extends RetCodeException
{
    public HttpStatusException(String message) {
        super(RetCode.HTTP_STATUS_ERROR, message);
    }

    public HttpStatusException( String message, Throwable cause) {
        super(RetCode.HTTP_STATUS_ERROR, message, cause);
    }


    public HttpStatusException(String code, String message) {
        super(code, message);
    }

    public HttpStatusException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
