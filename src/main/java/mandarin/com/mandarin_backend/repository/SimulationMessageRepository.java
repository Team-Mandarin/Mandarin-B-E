package mandarin.com.mandarin_backend.repository;

import mandarin.com.mandarin_backend.entity.SimulationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationMessageRepository extends JpaRepository<SimulationMessage, Long> {
    List<SimulationMessage> findBySimulationSimulationIdOrderByTimestampAsc(Long simulationId);
}

