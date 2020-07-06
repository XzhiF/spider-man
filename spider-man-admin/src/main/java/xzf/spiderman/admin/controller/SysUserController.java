package xzf.spiderman.admin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xzf.spiderman.admin.feign.SysUserFeignService;
import xzf.spiderman.admin.model.SysUser;

@RestController
@RequestMapping("/sys-user")
public class SysUserController implements SysUserFeignService
{
    @PostMapping("/login")
    @Override
    public SysUser login(String username, String password)
    {
        return new SysUser();
    }
}
