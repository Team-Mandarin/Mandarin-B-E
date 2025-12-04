package mandarin.com.mandarin_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_character_detaillog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report_Character_DetailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_report_id", nullable = false)
    private Chat_Report chatReport;

    @Column(columnDefinition = "json", nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
