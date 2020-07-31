package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class AuthorityException extends RetCodeException{

    public AuthorityException( String message) {
        super(RetCode.AUTHORITY_ERROR, message);
    }

    public AuthorityException( String message, Throwable cause) {
        super(RetCode.AUTHORITY_ERROR, message, cause);
    }


    public AuthorityException(String code, String message) {
        super(code, message);
    }

    public AuthorityException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
