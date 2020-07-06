package xzf.spiderman.scheduler.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AddTaskReq
{
    @NotBlank(message = "ID不能为空")
    private String id;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String description;

    private String spiderGroupId;

    private String jobClass;

    private String scheduleClass;

    private String scheduleProps;

    private String groupId;

    private List<TaskArgData> args = new ArrayList<>();
}
