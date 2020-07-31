package xzf.spiderman.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.admin.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, String>
{
}
