package xzf.spiderman.worker.service;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
@ToString
public class SpiderKey
{
    private final String spiderId;
    private final String groupId;
    private final String cnfId;
}
