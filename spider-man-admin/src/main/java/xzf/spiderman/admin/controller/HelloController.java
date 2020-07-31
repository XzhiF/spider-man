package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/admin/hello")
    public Map<String,Object> hello(@RequestParam("v") String v, HttpSession session)
    {
//        session.setAttribute("k","hahahaaaabc");
//
        session.setAttribute("v",v);
        return Collections.singletonMap("msg","admin hello, session-v="+v);
    }


    @GetMapping("/admin/get")
    public Map<String,Object> get(HttpSession session)
    {
        Object v = session.getAttribute("v");
        return Collections.singletonMap("msg","admin hello get,  v="+v);
    }

}
