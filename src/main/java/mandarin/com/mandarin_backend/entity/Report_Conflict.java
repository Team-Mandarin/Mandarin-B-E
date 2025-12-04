package mandarin.com.mandarin_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_conflict")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report_Conflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conflict_id")
    private Long conflictId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private User_Character character;

    @Column(name = "conflict_name", nullable = false, length = 100)
    private String conflictName;

    @Column(name = "danger_level", nullable = false)
    private int dangerLevel;

    @Column(name = "C_description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(columnDefinition = "text", nullable = false)
    private String solution;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
