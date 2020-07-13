package xzf.spiderman.worker.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ToString
public class SubmitSpiderReq
{
    @NotBlank
    private String groupId;
}
