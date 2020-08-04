package xzf.spiderman.starter.curator;

public class CuratorException extends RuntimeException
{
    public CuratorException() {
    }

    public CuratorException(String message) {
        super(message);
    }

    public CuratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CuratorException(Throwable cause) {
        super(cause);
    }

    public CuratorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
