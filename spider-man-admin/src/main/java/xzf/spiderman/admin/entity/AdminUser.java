package xzf.spiderman.admin.entity;

import lombok.Data;
import lombok.ToString;
import xzf.spiderman.admin.data.AdminUserData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "admin_user")
@Data
@ToString
public class AdminUser
{
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    @Id
    @Column(name = "username",nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private Integer enabled;


    public AdminUserData asData()
    {
        AdminUserData r = new AdminUserData();
        r.setUsername(username);
        r.setEnabled(enabled);
        return r;
    }


}
