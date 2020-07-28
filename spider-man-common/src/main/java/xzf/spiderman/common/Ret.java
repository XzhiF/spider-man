package xzf.spiderman.common;

import xzf.spiderman.common.exception.RetCodeException;

public class Ret<T>
{
    //
    private String code;
    private T data;


    // error
    private String errorMsg;
    private String errorType;

    public Ret()
    {
    }

    private Ret(String code, T data)
    {
        this.code = code;
        this.data = data;
    }

    private Ret(String code, T data, String errorMsg, String errorType)
    {
        this.code = code;
        this.data = data;
        this.errorMsg = errorMsg;
        this.errorType = errorType;
    }


    //

    public static <Void> Ret<Void> success()
    {
        return success(null);
    }
    public static <T> Ret<T> success(T data)
    {
        return new Ret<>(RetCode.SUCCESS, data);
    }

    public static <Void> Ret<Void> fail(Throwable throwable)
    {
        if(throwable instanceof RetCodeException){
            RetCodeException codeEx = (RetCodeException) throwable;
            return new Ret<>(codeEx.getCode(), null, codeEx.getMessage(), codeEx.getClass().getSimpleName());
        }

        return new Ret<>(RetCode.UNKNOWN_ERROR, null, throwable.getMessage(), throwable.getClass().getSimpleName());
    }

    //

    public boolean isSuccess()
    {
        return RetCode.SUCCESS.equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
