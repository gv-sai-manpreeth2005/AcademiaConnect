package org.cse.academiaconnect.service;

import jakarta.persistence.EntityNotFoundException;
import org.cse.academiaconnect.entity.AchievementBadge;
import org.cse.academiaconnect.repository.AchievementBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service class managing AchievementBadge business logic.
 * Uses constructor injection and handles clean CRUD operations.
 */
@Service
@Transactional
public class AchievementBadgeService {

    private final AchievementBadgeRepository badgeRepository;

    public AchievementBadgeService(AchievementBadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    /**
     * Award a badge to a user.
     * Prevents duplicate awards of the same badge name.
     */
    public AchievementBadge createAchievementBadge(AchievementBadge badge) {
        if (badgeRepository.existsByUserIdAndBadgeName(badge.getUser().getId(), badge.getBadgeName())) {
            throw new IllegalArgumentException("User has already earned the '" + badge.getBadgeName() + "' badge");
        }
        return badgeRepository.save(badge);
    }

    /**
     * Retrieve all Badges.
     */
    @Transactional(readOnly = true)
    public List<AchievementBadge> getAllAchievementBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Retrieve a Badge by ID.
     */
    @Transactional(readOnly = true)
    public AchievementBadge getAchievementBadgeById(Long id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Badge not found with ID: " + id));
    }

    /**
     * Update an Achievement Badge's details.
     */
    public AchievementBadge updateAchievementBadge(Long id, AchievementBadge badgeDetails) {
        AchievementBadge existingBadge = getAchievementBadgeById(id);
        existingBadge.setBadgeName(badgeDetails.getBadgeName());
        existingBadge.setDescription(badgeDetails.getDescription());
        existingBadge.setIconUrl(badgeDetails.getIconUrl());
        return badgeRepository.save(existingBadge);
    }

    /**
     * Delete/Revoke a Badge.
     */
    public void deleteAchievementBadge(Long id) {
        AchievementBadge badge = getAchievementBadgeById(id);
        badgeRepository.delete(badge);
    }
}