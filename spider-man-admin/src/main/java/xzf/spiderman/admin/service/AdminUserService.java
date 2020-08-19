package xzf.spiderman.admin.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.admin.data.*;
import xzf.spiderman.admin.entity.AdminUser;
import xzf.spiderman.admin.entity.AdminUserAuthority;
import xzf.spiderman.admin.repository.AdminUserAuthorityRepository;
import xzf.spiderman.admin.repository.AdminUserRepository;
import xzf.spiderman.common.exception.BizException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService
{
    @Autowired
    private AdminUserRepository userRepository;

    @Autowired
    private AdminUserAuthorityRepository authorityRepository;

    @Transactional
    public void add(AddAdminUserReq req)
    {
        if (userRepository.findById(req.getUsername()).isPresent()) {
            throw new BizException("用户名["+req.getUsername()+"]已存在");
        }

        String digestPass = DigestUtils.sha1Hex(req.getPassword().getBytes());

        AdminUser user = new AdminUser();
        user.setUsername(req.getUsername());
        user.setPassword(digestPass);
        user.setEnabled(AdminUser.ENABLED);

        List<AdminUserAuthority> authorities = req.getAuthorities().stream()
                .map(s->new AdminUserAuthority(req.getUsername(), s))
                .collect(Collectors.toList());


        userRepository.save(user);
        authorityRepository.saveAll(authorities);
    }


    @Transactional
    public void updatePass(UptAdminUserPassReq req)
    {
        AdminUser user = getUser(req.getUsername());
        String newPass = DigestUtils.sha1Hex(req.getPassword().getBytes());
        user.setPassword(newPass);

        userRepository.save(user);
    }

    @Transactional
    public void updateAuth(UptAdminUserAuthReq req)
    {
        List<AdminUserAuthority> authorities = req.getAuthorities().stream()
                .map(s->new AdminUserAuthority(req.getUsername(), s))
                .collect(Collectors.toList());

        authorityRepository.deleteAllByUsername(req.getUsername());
        authorityRepository.saveAll(authorities);
    }

    @Transactional
    public void enable(String username)
    {
        AdminUser user = getUser(username);
        user.setEnabled(AdminUser.ENABLED);
        userRepository.save(user);
    }

    @Transactional
    public void disable(String username)
    {
        AdminUser user = getUser(username);
        user.setEnabled(AdminUser.DISABLED);
        userRepository.save(user);
    }

    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public Page<AdminUserData> findAll(QryAdminUserReq req, Pageable pageable)
    {
        AdminUser qry = new AdminUser();
        qry.setUsername(req.getStartWithUsername());
        qry.setEnabled(req.getEqualsEnbaled());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("username",m->m.startsWith())
                .withMatcher("enabled", m->m.exact())
                .withIgnoreNullValues();

        Example<AdminUser> example = Example.of(qry, matcher);

        Page<AdminUser> src = userRepository.findAll(example, pageable);

        List<AdminUserData> list = src.getContent().stream().map(AdminUser::asData).collect(Collectors.toList());

        Page<AdminUserData> tar = new PageImpl<>(list, src.getPageable(), src.getTotalElements());

        return tar;
    }

    @Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
    public AdminUserData get(String username)
    {
        AdminUser adminUser = getUser(username);

        List<AdminUserAuthority> authorities = authorityRepository.findAllByUsername(username);
        List<String> authoritiesData = authorities.stream().map(AdminUserAuthority::getAuthority).collect(Collectors.toList());

        AdminUserData ret = adminUser.asData();

        ret.setAuthorities(authoritiesData);

        return ret;
    }



    private AdminUser getUser(String username)
    {
        return userRepository.findById(username).orElseThrow(()->new BizException("用户名["+username+"]未找到"));
    }

}
