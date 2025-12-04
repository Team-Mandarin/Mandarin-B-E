package mandarin.com.mandarin_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_report_score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat_Report_Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_report_score_id")
    private Long chatReportScoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_report_id", nullable = false)
    private Chat_Report chatReport;

    @Column(name = "label_key", nullable = false, length = 3)
    private String labelKey;  // F1, F2, F3, P1, P2, P3

    @Column(name = "label_score", nullable = false)
    private int labelScore;
}
