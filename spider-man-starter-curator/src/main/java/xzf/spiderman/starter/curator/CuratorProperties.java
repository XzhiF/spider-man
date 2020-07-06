package xzf.spiderman.starter.curator;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xzf.spiderman.curator")
public class CuratorProperties
{
    private String connectionString = "localhost:2181";
    private Integer connectionTimeoutMs = 10000;
    private Integer sessionTimeoutMs = 30000;
    private boolean autoStartup = true;


    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Integer getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(Integer connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public Integer getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }
}
