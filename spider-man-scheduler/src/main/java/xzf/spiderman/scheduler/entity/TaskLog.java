package xzf.spiderman.scheduler.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

@Entity
@Table(name = "task_log")
@Data
public class TaskLog
{
    public static int HAS_ERROR_TRUE = 1;
    public static int HAS_ERROR_FALSE = 0;

    @Id
    @Column(name = "task_log_id")
    private Long id;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "event", nullable = false)
    private String event;

    @Column(name = "create_time",nullable = false)
    private Date createTime;

    @Column(name = "has_error", nullable = false)
    private Integer hasError;

    @Column(name = "content")
    private String content;


    public static Long nextId()
    {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder id = new StringBuilder();
        id.append(now.getYear());
        id.append(now.getMonthValue() >= 10 ? now.getMonthValue() : "0"+now.getMonthValue());
        id.append(now.getDayOfMonth() >= 10 ? now.getMonthValue(): "0"+now.getDayOfMonth());
        id.append(now.getHour()>=10?now.getHour():"0"+now.getHour());
        id.append(now.getMinute()>=10?now.getMinute():"0"+now.getMinute());
        id.append(now.getSecond()>=10?now.getSecond():"0"+now.getSecond());

        Random r = new Random();

        id.append(r.nextInt(10));
        id.append(r.nextInt(10));
        id.append(r.nextInt(10));
        id.append(r.nextInt(10));

        return Long.valueOf(id.toString());
    }


}

