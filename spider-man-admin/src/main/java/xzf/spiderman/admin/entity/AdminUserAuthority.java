package xzf.spiderman.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "admin_user_authority")
@Data
public class AdminUserAuthority
{
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false)
    public String username;

    @Column(name = "authority")
    private String authority;

    public AdminUserAuthority() {
    }

    public AdminUserAuthority(String username, String authority){
        this.username = username;
        this.authority = authority;
    }
}
