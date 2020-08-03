package xzf.spiderman.scheduler.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import xzf.spiderman.scheduler.data.SaveTaskGroupReq;
import xzf.spiderman.scheduler.data.TaskGroupData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "task_group")
@Data
@ToString
@NoArgsConstructor
public class TaskGroup
{
    @Id
    @Column(name = "task_group_id", nullable = false)
    private String id;

    @Column(name = "task_group_name",nullable = false)
    private String name;

    @Column(name = "task_group_desc")
    private String desc;

    public static TaskGroup create(SaveTaskGroupReq req) {
        TaskGroup ret = new TaskGroup();
        BeanUtils.copyProperties(req,ret);
        return ret;
    }

    public void update(SaveTaskGroupReq req)
    {
        BeanUtils.copyProperties(req,this);
    }

    public TaskGroupData asData() {
        TaskGroupData ret = new TaskGroupData();
        BeanUtils.copyProperties(this,ret);
        return ret;
    }
}
