package mandarin.com.mandarin_backend.service;

import lombok.RequiredArgsConstructor;
import mandarin.com.mandarin_backend.dto.*;
import mandarin.com.mandarin_backend.entity.Simulation;
import mandarin.com.mandarin_backend.entity.SimulationMessage;
import mandarin.com.mandarin_backend.entity.UserCharacter;
import mandarin.com.mandarin_backend.repository.SimulationMessageRepository;
import mandarin.com.mandarin_backend.repository.SimulationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationMessageService {

    private final SimulationRepository simulationRepository;
    private final SimulationMessageRepository simulationMessageRepository;
    private final ChatService chatService;

    /**
     * 시뮬레이션 메시지를 저장하고 AI 답변을 생성/저장한 뒤 응답을 반환한다.
     *
     * @param request simulation_id, sender, content
     * @return AI 답변을 담은 ApiResponse
     */
    @Transactional
    public ApiResponse<SimulationMessageResponseDto> saveMessageAndReply(SimulationMessageRequestDto request) {
        // 기본 검증
        if (request.getSimulationId() == null) {
            return ApiResponse.fail("simulation_id가 없습니다.");
        }
        if (request.getSender() == null) {
            return ApiResponse.fail("sender 값이 없습니다.");
        }
        if (!StringUtils.hasText(request.getContent())) {
            return ApiResponse.fail("content가 비어 있습니다.");
        }

        Optional<Simulation> simulationOpt = simulationRepository.findById(request.getSimulationId());
        if (simulationOpt.isEmpty()) {
            return ApiResponse.fail("시뮬레이션을 찾을 수 없습니다.");
        }
        Simulation simulation = simulationOpt.get();

        // 1) 사용자/AI가 보낸 메시지 저장
        LocalDateTime now = LocalDateTime.now();
        SimulationMessage incoming = SimulationMessage.builder()
                .simulation(simulation)
                .sender(request.getSender())
                .content(request.getContent())
                .timestamp(now)
                .build();
        simulationMessageRepository.save(incoming);

        // Simulation의 last_update_time 동기화
        simulation.setLastUpdateTime(now);
        simulationRepository.save(simulation);

        // sender가 AI이면 추가 생성 없이 저장만 수행
        if (Boolean.TRUE.equals(request.getSender())) {
            return ApiResponse.success(
                    "AI 메시지가 저장되었습니다.",
                    SimulationMessageResponseDto.builder()
                            .aiReply(request.getContent())
                            .build()
            );
        }

        // 2) 이전 대화 이력 조회 (방금 저장한 메시지는 history에서 제외)
        List<SimulationMessage> historyMessages = simulationMessageRepository
                .findBySimulationSimulationIdOrderByTimestampAsc(simulation.getSimulationId());

        List<ChatLogDto> history = historyMessages.stream()
                .filter(msg -> !msg.getMessageId().equals(incoming.getMessageId()))
                .map(msg -> ChatLogDto.builder()
                        .role(Boolean.TRUE.equals(msg.getSender()) ? "assistant" : "user")
                        .content(msg.getContent())
                        .build())
                .collect(Collectors.toList());

        // 3) AI 호출 (기본 페르소나 생성)
        UserPersonaDto persona = buildPersonaFromCharacter(simulation.getCharacter());
        ChatResponseDto aiResponse = chatService.chat(persona, request.getContent(), history);

        // 4) AI 답변을 Simulation_Message에 저장
        LocalDateTime aiTime = LocalDateTime.now();
        SimulationMessage aiMessage = SimulationMessage.builder()
                .simulation(simulation)
                .sender(true)
                .content(aiResponse.getReply())
                .timestamp(aiTime)
                .build();
        simulationMessageRepository.save(aiMessage);

        // 5) Simulation last_update_time 갱신 (AI 답변 시각)
        simulation.setLastUpdateTime(aiTime);
        simulationRepository.save(simulation);

        return ApiResponse.success(
                "메시지 저장 및 AI 응답 생성 성공",
                SimulationMessageResponseDto.builder()
                        .aiReply(aiResponse.getReply())
                        .build()
        );
    }

    /**
     * 시뮬레이션 캐릭터 정보를 기반으로 기본 페르소나를 구성한다.
     * AI 서버의 필수 필드를 채우기 위한 기본값을 사용한다.
     */
    private UserPersonaDto buildPersonaFromCharacter(UserCharacter character) {
        UserPersonaDto persona = new UserPersonaDto();
        persona.setName(character != null && character.getCharacterName() != null
                ? character.getCharacterName()
                : "AI");

        SpeechStyleDto style = new SpeechStyleDto();
        style.setPolitenessLevel("상호존대");
        style.setTone("따뜻하고 공감하는 톤");
        style.setCommonEndings(defaultList("~요", "~네요", "~죠"));
        style.setFrequentInterjections(defaultList("음", "아", "맞아요"));

        EmojiStyleDto emoji = new EmojiStyleDto();
        emoji.setFrequency("Medium");
        emoji.setPreferredType("Text");
        emoji.setLaughSound("ㅎㅎ");
        style.setEmojiUsage(emoji);

        style.setDistinctiveHabits(defaultList("간결하게 답변", "예시를 들어 설명"));
        style.setSampleSentences(defaultList("무슨 일 있었어요?", "괜찮아요.", "함께 생각해볼게요."));

        persona.setSpeechStyle(style);
        return persona;
    }

    private List<String> defaultList(String... values) {
        List<String> list = new ArrayList<>();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }
}

