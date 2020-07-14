package xzf.spiderman.worker.service;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SpiderKey
{
    private final String spiderId;
    private final String groupId;
}
