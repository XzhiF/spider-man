package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
@ToString
public class SaveSpiderCnfReq
{
    @NotBlank(message = "ID不能为空")
    private String id ;

    @NotBlank(message = "分组不能为空")
    private String groupId;

    @NotBlank(message = "服务器不能为空")
    private String serverId;

    @NotEmpty(message = "需要配置存储库")
    private List<String> storeIds;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotNull(message = "类型不能为空")
    private Integer type;

    private String params;

    private String desc;

    @NotBlank(message = "爬虫处理程序不能为空")
    private String processor;

    private Integer maxPollTimeoutCount;

    private Integer pollTimeoutSeconds;

    private Integer workerThreads;

    @NotNull(message = "爬虫任务模式不能为空")
    private Integer mode;


}
