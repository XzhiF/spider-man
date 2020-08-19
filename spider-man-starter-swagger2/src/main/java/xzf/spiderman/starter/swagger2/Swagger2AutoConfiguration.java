package xzf.spiderman.starter.swagger2;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;


/**
 * 参考   https://gitee.com/wxdfun/sw
 */
@EnableConfigurationProperties(Swagger2Properties.class)
@ConditionalOnProperty(value = "xzf.spiderman.swagger2.enabled", havingValue = "true")
@Configuration
@Import({Swagger2DocumentationConfiguration.class})
public class Swagger2AutoConfiguration
{
    @Autowired
    private Swagger2Properties properties;

    @Autowired(required = false)
    private List<DocketCustomizer> customizers = new ArrayList<>();

    @Bean
    public Docket createRestApi() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2);

        // path mapping


        // selector
        ApiSelectorBuilder builder = docket.apiInfo(apiInfo())
                .select();

        if(properties.getBasePackage() != null){
            builder.apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()));
        }  else  {
            builder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        }
        builder.paths(PathSelectors.any());

        // rebuild
        docket = builder.build();

        if(customizers != null){
            for (DocketCustomizer customizer : customizers) {
                customizer.customize(docket);
            }
        }

        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getApiInfo().getTitle())
                .description(properties.getApiInfo().getDescription())
                .termsOfServiceUrl(properties.getApiInfo().getTermsOfServiceUrl())
                .contact(new Contact(properties.getApiInfo().getContact().getName(), properties.getApiInfo().getContact().getUrl(), properties.getApiInfo().getContact().getEmail()))
                .version(properties.getApiInfo().getVersion())
                .build();
    }


    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                AlternateTypeRule alternateTypeRule = newRule(resolver.resolve(Pageable.class), resolver.resolve(Page.class));
                return Arrays.asList(alternateTypeRule);
            }
        };
    }

    @ApiModel
    static class Page {
//        @ApiModelProperty("Results page you want to retrieve (0..N)")
        @ApiModelProperty("页码，从0开始")
        private Integer page;

//        @ApiModelProperty("Number of records per page")
        @ApiModelProperty("页大小")
        private Integer size;

        @ApiModelProperty("Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
        private List<String> sort;

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public List<String> getSort() {
            return sort;
        }

        public void setSort(List<String> sort) {
            this.sort = sort;
        }
    }



}
