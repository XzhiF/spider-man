package xzf.spiderman.starter.swagger2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.service.Contact;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "xzf.spiderman.swagger2")
public class Swagger2Properties
{
    private String basePackage;

    private ApiInfo apiInfo = new ApiInfo();

    @Data
    public class ApiInfo
    {
        private String title;
        private String description;
        private String termsOfServiceUrl;
        private String version;
        private Contact contact = new Contact();
    }
    @Data
    public class Contact
    {
        private  String name;
        private  String url;
        private  String email;
    }
}
