package xzf.spiderman.starter.swagger2;

import springfox.documentation.spring.web.plugins.Docket;

public interface DocketCustomizer
{
    void customize(Docket docker);
}
