package xzf.spiderman.admin.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UptAdminUserAuthReq
{
    private String username;
    List<String> authorities = new ArrayList<>();


}
