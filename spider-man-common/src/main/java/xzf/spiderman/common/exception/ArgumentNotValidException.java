package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class ArgumentNotValidException extends RetCodeException
{

    public ArgumentNotValidException(String message) {
        super(RetCode.ARGUMENT_NOT_VALID_ERROR, message);
    }

    public ArgumentNotValidException(String message, Throwable cause) {
        super(RetCode.ARGUMENT_NOT_VALID_ERROR, message, cause);
    }

    public ArgumentNotValidException(String code, String message) {
        super(code, message);
    }

    public ArgumentNotValidException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
