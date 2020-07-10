package xzf.spiderman.worker.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "xzf.spiderman.worker")
public class WorkerProperties
{
    private Long pollTimeout = 100L;
    private TimeUnit pollTimeunit = TimeUnit.SECONDS;

    public Long getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(Long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public TimeUnit getPollTimeunit() {
        return pollTimeunit;
    }

    public void setPollTimeunit(String pollTimeunit) {
        this.pollTimeunit = TimeUnit.valueOf(pollTimeunit);
    }
}
