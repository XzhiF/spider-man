package xzf.spiderman.common.configuration.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xzf.spiderman.common.Ret;
import xzf.spiderman.common.exception.ArgumentNotValidException;

@Configuration
public class SpiderManExceptionHandlerConfiguration
{
    @RestControllerAdvice
    @Slf4j
    public static class RestControllerAdviceConfiguration
    {
        @ExceptionHandler(Exception.class)
        public Ret<Void> handle(Exception ex)
        {
            log.error(ex.getMessage(), ex);

            if(ex instanceof MethodArgumentNotValidException){
                MethodArgumentNotValidException mex = (MethodArgumentNotValidException)ex;
                return Ret.fail( asArgumentNotValidException(mex) );
            }

            return Ret.fail(ex);
        }

        private ArgumentNotValidException asArgumentNotValidException(MethodArgumentNotValidException mex)
        {
            return new ArgumentNotValidException("参数校验错误：" + mex.getBindingResult().getFieldError().getDefaultMessage());
        }
    }

}
