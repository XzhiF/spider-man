package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import xzf.spiderman.common.exception.BizException;

import java.util.Objects;

@Data
@ToString
@EqualsAndHashCode(of = "storeId")
public class StoreCnfData
{
    private String cnfId;

    private String storeId;

    private String host;

    private Integer port;

    private String tableName;

    private Integer type;       // 1.mongo... 2.es

    private String url;         // mongon:,

    private String username ;

    private String password;

    private String database;



    public boolean hasChanges(StoreCnfData other)
    {
        if(other==null){
            throw new BizException("非法调用hasChanges, other is null ");
        }
        if(!Objects.equals(this, other)){
            throw new BizException("非法调用hasChanges,不同的storeId["+this.storeId+","+other.storeId+"]");
        }

        if(!StringUtils.equals(this.host, other.host)){
           return true;
        }
        if(!StringUtils.equals(this.port+"", other.port+"")){
            return true;
        }
        if(!StringUtils.equals(this.tableName, other.tableName)){
            return true;
        }
        if(!StringUtils.equals(this.url, other.url)){
            return true;
        }
        if(!StringUtils.equals(this.username, other.username)){
            return true;
        }
        if(!StringUtils.equals(this.password, other.password)){
            return true;
        }
        if(!StringUtils.equals(this.database, other.database)){
            return true;
        }

        return false;
    }



}
