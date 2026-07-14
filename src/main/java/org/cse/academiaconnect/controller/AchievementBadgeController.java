package org.cse.academiaconnect.controller;

import jakarta.validation.Valid;
import org.cse.academiaconnect.entity.AchievementBadge;
import org.cse.academiaconnect.service.AchievementBadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class AchievementBadgeController {

    private final AchievementBadgeService achievementBadgeService;

    public AchievementBadgeController(AchievementBadgeService achievementBadgeService) {
        this.achievementBadgeService = achievementBadgeService;
    }

    @PostMapping
    public ResponseEntity<AchievementBadge> createAchievementBadge(@Valid @RequestBody AchievementBadge badge) {
        return new ResponseEntity<>(achievementBadgeService.createAchievementBadge(badge), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AchievementBadge>> getAllAchievementBadges() {
        return ResponseEntity.ok(achievementBadgeService.getAllAchievementBadges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementBadge> getAchievementBadgeById(@PathVariable Long id) {
        return ResponseEntity.ok(achievementBadgeService.getAchievementBadgeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementBadge> updateAchievementBadge(@PathVariable Long id,
                                                                   @Valid @RequestBody AchievementBadge badge) {
        return ResponseEntity.ok(achievementBadgeService.updateAchievementBadge(id, badge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAchievementBadge(@PathVariable Long id) {
        achievementBadgeService.deleteAchievementBadge(id);
        return ResponseEntity.ok("Achievement badge deleted successfully.");
    }
}