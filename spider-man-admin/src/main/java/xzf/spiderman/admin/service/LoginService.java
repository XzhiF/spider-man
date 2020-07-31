package xzf.spiderman.admin.service;

import com.alibaba.nacos.common.utils.Md5Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xzf.spiderman.admin.data.LoginReq;
import xzf.spiderman.admin.data.SessionAdminUser;
import xzf.spiderman.admin.entity.AdminUser;
import xzf.spiderman.admin.entity.AdminUserAuthority;
import xzf.spiderman.admin.repository.AdminUserAuthorityRepository;
import xzf.spiderman.admin.repository.AdminUserRepository;
import xzf.spiderman.common.exception.AuthorityException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService
{
    @Autowired
    private AdminUserRepository adminUserRepository;
    @Autowired
    private AdminUserAuthorityRepository adminUserAuthorityRepository;

    public SessionAdminUser login(LoginReq req)
    {
        AdminUser adminUser = adminUserRepository.findById(req.getUsername())
                .orElseThrow(()->new AuthorityException("没有此用户"));

        if(adminUser.getEnabled().intValue()==AdminUser.DISABLED){
            throw new AuthorityException("用户已经禁用");
        }
        
        String password = adminUser.getPassword();
        String reqPass = DigestUtils.sha1Hex(req.getPassword().getBytes());

        if(!StringUtils.equals(password, reqPass)){
            throw new  AuthorityException("密码错误");
        }

        SessionAdminUser r = new SessionAdminUser();
        r.setUsername(adminUser.getUsername());
        r.setAuthorities(getAllAuthorities(adminUser.getUsername()));

        return r;
    }

    private List<String> getAllAuthorities(String username)
    {
        List<AdminUserAuthority> authorities = adminUserAuthorityRepository.findAllByUsername(username);
        return authorities.stream().map(AdminUserAuthority::getAuthority).collect(Collectors.toList());
    }


    public static void main(String[] args) {
        //7c4a8d09ca3762af61e59520943dc26494f8941b
//        System.out.println(Sha2Crypt.sha256Crypt("123456".getBytes(),"rounds=8"));

        System.out.println(DigestUtils.sha1Hex("123456".getBytes()));
    }



}
