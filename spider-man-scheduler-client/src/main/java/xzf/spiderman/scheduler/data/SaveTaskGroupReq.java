package xzf.spiderman.scheduler.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SaveTaskGroupReq
{

    @NotBlank(message = "ID不能为空")
    private String id;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String desc;
}
