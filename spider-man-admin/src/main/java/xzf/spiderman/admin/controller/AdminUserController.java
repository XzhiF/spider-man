package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.admin.data.AddAdminUserReq;
import xzf.spiderman.admin.data.UptAdminUserAuthReq;
import xzf.spiderman.admin.data.UptAdminUserPassReq;
import xzf.spiderman.admin.service.AdminUserService;
import xzf.spiderman.common.Ret;

import javax.validation.Valid;

@RestController
public class AdminUserController
{
    @Autowired
    public AdminUserService adminUserService;

    @PostMapping("/admin/admin-user/add")
    public Ret<Void> add(@Valid @RequestBody AddAdminUserReq req)
    {
        adminUserService.add(req);
        return Ret.success();
    }


    @PostMapping("/admin/admin-user/update-pass")
    public Ret<Void> updatePass(@Valid @RequestBody UptAdminUserPassReq req)
    {
        adminUserService.updatePass(req);
        return Ret.success();
    }


    @PostMapping("/admin/admin-user/update-auth")
    public Ret<Void> updateAuth(@Valid @RequestBody UptAdminUserAuthReq req)
    {
        adminUserService.updateAuth(req);
        return Ret.success();
    }

    @PostMapping("/admin/admin-user/enable/{username}")
    public Ret<Void> enable(@PathVariable("username") String username)
    {
        adminUserService.enable(username);
        return Ret.success();
    }

    @PostMapping("/admin/admin-user/disable/{username}")
    public Ret<Void> disable(@PathVariable("username") String username)
    {
        adminUserService.disable(username);
        return Ret.success();
    }


}
