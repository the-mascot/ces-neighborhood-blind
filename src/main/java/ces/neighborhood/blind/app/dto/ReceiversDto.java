package ces.neighborhood.blind.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiversDto {

    private String name;

    private String phoneNum;

    private String relation;

}
