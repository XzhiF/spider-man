package xzf.spiderman.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xzf.spiderman.admin.data.SessionAdminUser;
import xzf.spiderman.common.Ret;

import javax.servlet.http.HttpSession;

@RestController
public class LogoutController
{

    @RequestMapping(value = "/admin/logout", method = {RequestMethod.GET,RequestMethod.POST})
    public Ret<Void> logout()
    {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        session.removeAttribute(SessionAdminUser.SESSION_KEY);
        return Ret.success();
    }
}
