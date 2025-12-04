package mandarin.com.mandarin_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_detaillog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report_DetailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conflict_id", nullable = false)
    private Report_Conflict conflict;

    @Column(nullable = false, length = 10)
    private String sender;  // USER / CHARACTER

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
