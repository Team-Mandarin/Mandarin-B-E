package mandarin.com.mandarin_backend.service;

import lombok.RequiredArgsConstructor;
import mandarin.com.mandarin_backend.dto.ChatReportCreateRequestDto;
import mandarin.com.mandarin_backend.dto.ChatReportResponseDto;
import mandarin.com.mandarin_backend.entity.ChatReport;
import mandarin.com.mandarin_backend.entity.ChatReportAvg;
import mandarin.com.mandarin_backend.entity.Simulation;
import mandarin.com.mandarin_backend.entity.User;
import mandarin.com.mandarin_backend.entity.UserCharacter;
import mandarin.com.mandarin_backend.repository.ChatReportAvgRepository;
import mandarin.com.mandarin_backend.repository.ChatReportRepository;
import mandarin.com.mandarin_backend.repository.SimulationRepository;
import mandarin.com.mandarin_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatReportService {

    private final ChatReportRepository chatReportRepository;
    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;
    private final ChatReportAvgRepository chatReportAvgRepository;

    // 리포트 단건 조회
    public ChatReportResponseDto getChatReportById(Integer id) {
        ChatReport report = chatReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리포트 정보가 없습니다."));

        return ChatReportResponseDto.fromEntity(report);
    }

    // 유저별 리포트 조회
    public List<ChatReportResponseDto> getChatReportsByUserId(Long userId) {
        List<ChatReport> list = chatReportRepository.findByUser_Id(userId);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("해당 유저의 리포트가 없습니다.");
        }

        return list.stream()
                .map(ChatReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 캐릭터별 리포트 조회
    public List<ChatReportResponseDto> getChatReportsByCharacterId(Long characterId) {
        List<ChatReport> list = chatReportRepository.findByCharacter_CharacterId(characterId);

        if (list.isEmpty()) {
            throw new IllegalArgumentException("해당 캐릭터의 리포트가 없습니다.");
        }

        return list.stream()
                .map(ChatReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 리포트 생성
    public Integer createChatReport(ChatReportCreateRequestDto request) {

        // 1. Simulation 조회
        Simulation simulation = simulationRepository.findById(request.getSimulation_id())
                .orElseThrow(() -> new IllegalArgumentException("Simulation not found"));

        // 2. User 조회
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 3. 캐릭터
        UserCharacter character = simulation.getCharacter();

        // 4. ChatReport 생성
        ChatReport report = ChatReport.builder()
                .simulation(simulation)
                .user(user)
                .character(character)
                .scoreAvg(80)          // TODO: AI 계산값 넣기
                .labelKey(1)           // TODO
                .labelScore(50)        // TODO
                .reportContent("{}")   // TODO
                .build();

        chatReportRepository.save(report);

        // 5. Simulation 종료 처리
        simulation.setIsFinished(true);
        simulationRepository.save(simulation);

        // 6. ChatReportAvg 업데이트
        Optional<ChatReportAvg> avgOpt = chatReportAvgRepository.findByUser_Id(user.getId());

        // labelKey를 안전하게 F1~F6으로 변환
        Integer labelKey = report.getLabelKey();
        if (labelKey == null || labelKey < 1 || labelKey > 6) {
            labelKey = 1; // 기본값
        }
        ChatReportAvg.TotalLabelKey totalLabelKey = ChatReportAvg.TotalLabelKey.valueOf("F" + labelKey);

        if (avgOpt.isEmpty()) {
            // 새로 생성
            ChatReportAvg avg = ChatReportAvg.builder()
                    .user(user)
                    .chatReport(report)
                    .avgMandarinScore(report.getScoreAvg())
                    .totalLabelKey(totalLabelKey)
                    .totalLabelScore(report.getLabelScore())
                    .build();

            chatReportAvgRepository.save(avg);

        } else {
            // 기존 평균 업데이트
            ChatReportAvg avg = avgOpt.get();

            Integer newAvg = (avg.getAvgMandarinScore() + report.getScoreAvg()) / 2;

            avg.setAvgMandarinScore(newAvg);
            avg.setChatReport(report);
            avg.setTotalLabelKey(totalLabelKey);
            avg.setTotalLabelScore(report.getLabelScore());

            chatReportAvgRepository.save(avg);
        }

        return report.getChatReportId();
    }
