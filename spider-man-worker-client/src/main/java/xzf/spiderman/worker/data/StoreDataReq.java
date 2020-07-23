package xzf.spiderman.worker.data;

import lombok.*;

import java.util.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDataReq
{
    public static final int TYPE_MONGO = 1;


    private Map<String,Object> data;
    private List<StoreCnfData> storeCnfs;
    private Date timestamp;



    public StoreCnfData findCnfByType(int type)
    {
        if(storeCnfs == null ){
            return null;
        }
        return storeCnfs.stream().filter(s->s.getType().equals(type)).findFirst().orElse(null);
    }

}
