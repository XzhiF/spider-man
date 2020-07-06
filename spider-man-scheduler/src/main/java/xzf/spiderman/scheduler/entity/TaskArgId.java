package xzf.spiderman.scheduler.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"taskId","key"})
public class TaskArgId implements Serializable
{
    private String taskId;
    private String key;
}
