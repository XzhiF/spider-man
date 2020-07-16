package xzf.spiderman.worker.service;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GroupSpiderKey
{
    private final String spiderId;
    private final String groupId;

    public SpiderKey toSpiderKey(String cnfId)
    {
        return new SpiderKey(spiderId, groupId, cnfId);
    }
}
