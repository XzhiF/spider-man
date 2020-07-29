package xzf.spiderman.common.configuration.webflux;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SpiderManWebFluxCookieConfiguration.class)
public @interface EnableSpiderManWebFluxCookie
{
}
