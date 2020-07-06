package xzf.spiderman.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.scheduler.entity.TaskLog;

import java.time.LocalDateTime;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long>
{

    default Long nextId()
    {
        LocalDateTime now = LocalDateTime.now();

        StringBuilder id = new StringBuilder();
        id.append("");


        return null;
    }

    public static void main(String[] args) {

        LocalDateTime now = LocalDateTime.now();
        StringBuilder id = new StringBuilder();
        id.append("");

    }

}
