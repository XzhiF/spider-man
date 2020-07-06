package xzf.spiderman.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import xzf.spiderman.admin.model.SysUser;

@FeignClient("spider-man-admin")
public interface SysUserFeignService
{
    @PostMapping("/login")
    SysUser login(String username , String password);
}
