package xzf.spiderman.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.scheduler.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String>
{
    List<Task> findAllByActiveFlag(int activeFlag);

    @Modifying
    @Query("update Task t set t.status = 0")
    int updateAllStatusToStop();


    List<Task> findAllByGroupIdAndActiveFlag(String groupId,int activeFlag);
}
