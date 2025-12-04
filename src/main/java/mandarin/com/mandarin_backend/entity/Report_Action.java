package mandarin.com.mandarin_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report_Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private User_Character character;

    @Column(name = "action_name", nullable = false, length = 100)
    private String actionName;

    @Column(name = "act_description", nullable = false, columnDefinition = "text")
    private String actionDescription;

    @Column(name = "check_active", nullable = false)
    private Boolean checkActive;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
