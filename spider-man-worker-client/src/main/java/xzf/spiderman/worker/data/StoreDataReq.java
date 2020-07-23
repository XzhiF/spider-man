package xzf.spiderman.worker.data;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
@Builder
public class StoreDataReq
{
    private Map<String,Object> data = new HashMap<>();
    private List<StoreCnfData> storeCnfs = new ArrayList<>();
}
