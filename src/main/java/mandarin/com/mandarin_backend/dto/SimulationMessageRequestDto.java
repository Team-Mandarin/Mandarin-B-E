package mandarin.com.mandarin_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationMessageRequestDto {
    private Long simulationId;
    private Boolean sender;
    private String content;
}

