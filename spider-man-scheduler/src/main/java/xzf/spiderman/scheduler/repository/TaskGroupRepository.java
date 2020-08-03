package xzf.spiderman.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.scheduler.entity.TaskGroup;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, String>
{

    @Query("select count(o) from Task o where o.groupId=?1")
    int usingCount(String id);
}
