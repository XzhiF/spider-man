package xzf.spiderman.scheduler.data;

import lombok.Data;

@Data
public class TaskArgData
{
    private String key;
    private String value;

    public TaskArgData() {
    }

    public TaskArgData(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
}
