package mandarin.com.mandarin_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat_Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_report_id")
    private Long chatReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private User_Character character;

    @Column(name = "chat_report_name", nullable = false, length = 255)
    private String chatReportName;

    @Column(name = "report_type", nullable = false, length = 10)
    private String reportType;  // FUTURE / PAST

    @Column(name = "avg_score", nullable = false)
    private int avgScore;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
