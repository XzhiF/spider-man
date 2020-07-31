package xzf.spiderman.admin.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginReq implements Serializable
{

    @NotBlank(message = "{loginreq.username.NotBlank.message}")
    private String username;
    @NotBlank(message = "{loginreq.password.NotBlank.message}")
    private String password;
}
