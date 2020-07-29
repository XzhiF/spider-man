package xzf.spiderman.scheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/scheduler/hello")
    public Map<String,Object> hello(HttpSession session)
    {
        Object sessionVal = session.getAttribute("k");
        return Collections.singletonMap("msg","scheduler hello , session val="+sessionVal);
    }

}
