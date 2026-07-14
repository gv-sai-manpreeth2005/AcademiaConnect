package org.cse.academiaconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Entity
@Table(name = "achievement_badges")
public class AchievementBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Badge name is required")
    @Size(max = 100, message = "Badge name cannot exceed 100 characters")
    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;

    @NotBlank(message = "Badge description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(nullable = false)
    private String description;

    @Size(max = 255, message = "Icon URL cannot exceed 255 characters")
    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;

    // Relationships
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}