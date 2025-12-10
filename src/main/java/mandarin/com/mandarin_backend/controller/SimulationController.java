package mandarin.com.mandarin_backend.controller;

import lombok.RequiredArgsConstructor;
import mandarin.com.mandarin_backend.dto.ApiResponse;
import mandarin.com.mandarin_backend.dto.SimulationMessageRequestDto;
import mandarin.com.mandarin_backend.dto.SimulationMessageResponseDto;
import mandarin.com.mandarin_backend.dto.SimulationResponseDto;
import mandarin.com.mandarin_backend.service.SimulationMessageService;
import mandarin.com.mandarin_backend.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;
    private final SimulationMessageService simulationMessageService;

    // 시뮬레이션 다건 조회 API
    // GET /simulation/character/{character_id}
    @GetMapping("/character/{character_id}")
    public ResponseEntity<ApiResponse<List<SimulationResponseDto>>> getSimulationsByCharacterId(
            @PathVariable("character_id") Long characterId) {

        ApiResponse<List<SimulationResponseDto>> response = 
                simulationService.getSimulationsByCharacterId(characterId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response); // 200 OK
        }

        return ResponseEntity.badRequest().body(response); // 실패 시 400
    }

    /**
     * 시뮬레이션 메시지 저장 & AI 답변 생성
     * POST /simulation/message
     */
    @PostMapping("/message")
    public ResponseEntity<ApiResponse<SimulationMessageResponseDto>> createSimulationMessage(
            @RequestBody SimulationMessageRequestDto request) {

        ApiResponse<SimulationMessageResponseDto> response =
                simulationMessageService.saveMessageAndReply(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}

