package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class ConfigNotValidException extends RetCodeException
{
    public ConfigNotValidException(String message) {
        super(RetCode.CONFIG_NOT_VALID_ERROR, message);
    }

    public ConfigNotValidException(String message, Throwable cause) {
        super(RetCode.CONFIG_NOT_VALID_ERROR, message, cause);
    }

    public ConfigNotValidException(String code, String message) {
        super(code, message);
    }

    public ConfigNotValidException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
