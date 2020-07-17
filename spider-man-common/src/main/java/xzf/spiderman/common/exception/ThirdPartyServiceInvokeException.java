package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class ThirdPartyServiceInvokeException extends RetCodeException
{
    public ThirdPartyServiceInvokeException(String message) {
        super(RetCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, message);
    }

    public ThirdPartyServiceInvokeException(String message, Throwable cause) {
        super(RetCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, message, cause);
    }

    public ThirdPartyServiceInvokeException(String code, String message) {
        super(code, message);
    }

    public ThirdPartyServiceInvokeException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
