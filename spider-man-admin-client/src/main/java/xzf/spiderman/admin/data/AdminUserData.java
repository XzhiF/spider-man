package xzf.spiderman.admin.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminUserData
{
    private String username;
    private Integer enabled;

    // only for detail
    private List<String> authorities = new ArrayList<>();
}
