package xzf.spiderman.common.exception;

public abstract class RetCodeException extends RuntimeException
{
    private String code;

    public RetCodeException(String code, String message)
    {
        super(message);
        this.code = code;
    }

    public RetCodeException(String code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }


    public String getCode()
    {
        return code;
    }
}
