package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/admin/hello")
    public Map<String,Object> hello()
    {
        return Collections.singletonMap("msg",dataSource);
    }
}
