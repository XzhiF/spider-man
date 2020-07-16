package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class ThirdPartyInvokeException extends RetCodeException
{
    public ThirdPartyInvokeException( String message) {
        super(RetCode.THIRD_PARTY_INVOKE_ERROR, message);
    }

    public ThirdPartyInvokeException(String message, Throwable cause) {
        super(RetCode.THIRD_PARTY_INVOKE_ERROR, message, cause);
    }

    public ThirdPartyInvokeException(String code, String message) {
        super(code, message);
    }

    public ThirdPartyInvokeException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
