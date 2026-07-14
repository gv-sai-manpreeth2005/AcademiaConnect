package org.cse.academiaconnect.repository;

import org.cse.academiaconnect.entity.AchievementBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for AchievementBadge entity operations.
 */

public interface AchievementBadgeRepository extends JpaRepository<AchievementBadge, Long> {

    /**
     * Find all achievement badges earned by a user.
     */
    List<AchievementBadge> findByUserId(Long userId);

    /**
     * Find all achievement badges earned by a user, ordered from newest to oldest.
     */
    List<AchievementBadge> findByUserIdOrderByEarnedAtDesc(Long userId);

    /**
     * Check if a user has already earned a specific badge by name.
     */
    boolean existsByUserIdAndBadgeName(Long userId, String badgeName);
}