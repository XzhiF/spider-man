package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
    public Ret<SessionAdminUser> login(@Valid @RequestBody LoginReq req)
    {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        SessionAdminUser data = loginService.login(req);
        session.setAttribute(SessionAdminUser.SESSION_KEY, data);
        return Ret.success(data);
    }


}
