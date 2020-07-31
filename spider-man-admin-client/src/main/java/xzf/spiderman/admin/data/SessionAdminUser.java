package xzf.spiderman.admin.data;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SessionAdminUser
{
    public static final String SESSION_KEY = "SESSION_ADMIN_USER";

    private String username;
    private List<String> authorities;
}
