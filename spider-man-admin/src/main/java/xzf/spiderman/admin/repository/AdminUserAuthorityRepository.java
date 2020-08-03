package xzf.spiderman.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.admin.entity.AdminUserAuthority;

import java.util.List;

public interface AdminUserAuthorityRepository extends JpaRepository<AdminUserAuthority, Long>
{
    List<AdminUserAuthority> findAllByUsername(String username);

    void deleteAllByUsername(String username);
}
