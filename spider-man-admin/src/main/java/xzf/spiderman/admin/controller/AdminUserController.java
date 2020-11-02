package xzf.spiderman.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import xzf.spiderman.admin.data.*;
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


    @GetMapping("/admin/admin-user/get/{username}")
    public Ret<AdminUserData> get(@PathVariable("username") String username)
    {
        AdminUserData data = adminUserService.get(username);
        return Ret.success(data);
    }

    @GetMapping("/admin/admin-user/list")
    public Ret<Page<AdminUserData>> list(QryAdminUserReq req, @PageableDefault Pageable pageable)
    {
        Page<AdminUserData> data = adminUserService.findAll(req, pageable);
        return Ret.success(data);
    }


}
