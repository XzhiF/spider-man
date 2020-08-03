package xzf.spiderman.worker.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SaveSpiderGroupReq
{

    @NotBlank(message = "id不能为空")
    private String id;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String desc;

}
