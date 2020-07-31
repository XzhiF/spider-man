package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.admin.data.LoginReq;
import xzf.spiderman.admin.data.SessionAdminUser;
import xzf.spiderman.admin.service.LoginService;
import xzf.spiderman.common.Ret;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class LoginController
{

    @Autowired
    private LoginService loginService;

    @PostMapping("/admin/login")
    public Ret<SessionAdminUser> login(@Valid @RequestBody LoginReq req, HttpSession session)
    {
        SessionAdminUser data = loginService.login(req);
        session.setAttribute(SessionAdminUser.SESSION_KEY, data);
        return Ret.success(data);
    }

    @PostMapping("/admin/test1")
    public Ret<String> test1(@RequestParam("m") String m)
    {
        return Ret.success(m);
    }

    @PostMapping("/admin/test2")
    public Ret<String> test2(@RequestParam("m") String m)
    {
        return Ret.success(m);
    }




}
