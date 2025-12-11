package mandarin.com.mandarin_backend.repository;

import mandarin.com.mandarin_backend.entity.ChatReportAvg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatReportAvgRepository extends JpaRepository<ChatReportAvg, Long> {

    Optional<ChatReportAvg> findByUser_Id(Long userId);
}
