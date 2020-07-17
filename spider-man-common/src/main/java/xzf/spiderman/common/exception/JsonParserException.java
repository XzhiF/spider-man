package xzf.spiderman.common.exception;

import xzf.spiderman.common.RetCode;

public class JsonParserException  extends RetCodeException
{

    public JsonParserException( String message) {
        super(RetCode.JSON_PARSER_ERROR, message);
    }

    public JsonParserException( String message, Throwable cause) {
        super(RetCode.JSON_PARSER_ERROR, message, cause);
    }

    public JsonParserException(String code, String message) {
        super(code, message);
    }

    public JsonParserException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
