package xzf.spiderman.common.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SpiderManSessionConfiguration.class)
public @interface EnableSpiderManSession
{
}
