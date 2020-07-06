package xzf.spiderman.scheduler.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "task_arg")
@IdClass(TaskArgId.class)
public class TaskArg
{
    @Id
    @Column(name = "task_id")
    private String taskId;

    @Id
    @Column(name = "arg_key")
    private String key;

    @Column(name = "arg_value")
    private String value;



    public static Map<String,Object> toMap(List<TaskArg> args)
    {
        Map<String,Object> results = new HashMap<>();
        for (TaskArg arg : args) {
            results.put(arg.getKey(), arg.getValue());
        }
        return results;
    }

}
