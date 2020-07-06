package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class BizException extends RetCodeException
{

    public BizException(String message)
    {
        super(RetCode.BIZ_ERROR, message);
    }

    public BizException(String message, Throwable cause)
    {
        super(RetCode.BIZ_ERROR, message, cause);
    }

    public BizException(String code, String message)
    {
        super(code, message);
    }

    public BizException(String code, String message, Throwable cause)
    {
        super(code, message, cause);
    }


}
