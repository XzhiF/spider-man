package xzf.spiderman.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.scheduler.entity.TaskArg;
import xzf.spiderman.scheduler.entity.TaskArgId;

import java.util.List;

public interface TaskArgRepository  extends JpaRepository<TaskArg, TaskArgId>
{
    void deleteAllByTaskId(String taskId);

    List<TaskArg> findAllByTaskId(String taskId);
}
