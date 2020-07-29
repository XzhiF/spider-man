package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/admin/hello")
    public Map<String,Object> hello(HttpSession session)
    {
        session.setAttribute("k","hahaha");
        session.setAttribute("map", Collections.singletonMap("msg","hahakkk"));

        return Collections.singletonMap("msg","admin hello");
    }
}
