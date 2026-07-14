package org.cse.academiaconnect.service;

import jakarta.persistence.EntityNotFoundException;
import org.cse.academiaconnect.entity.Activity;
import org.cse.academiaconnect.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service class managing Academic Activity business logic.
 * Uses constructor injection and handles clean CRUD operations.
 */
@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Create a new Activity.
     */
    public Activity createActivity(Activity activity) {
        if (activity.getRegistrationDeadline().isAfter(activity.getDateTime())) {
            throw new IllegalArgumentException("Registration deadline must be before the activity scheduled time");
        }
        return activityRepository.save(activity);
    }

    /**
     * Retrieve all Activities.
     */
    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * Retrieve an Activity by ID.
     */
    @Transactional(readOnly = true)
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with ID: " + id));
    }

    /**
     * Update an existing Activity's details.
     */
    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity existingActivity = getActivityById(id);

        if (activityDetails.getRegistrationDeadline().isAfter(activityDetails.getDateTime())) {
            throw new IllegalArgumentException("Registration deadline must be before the activity scheduled time");
        }

        existingActivity.setTitle(activityDetails.getTitle());
        existingActivity.setDescription(activityDetails.getDescription());
        existingActivity.setType(activityDetails.getType());
        existingActivity.setDateTime(activityDetails.getDateTime());
        existingActivity.setLocation(activityDetails.getLocation());
        existingActivity.setCapacity(activityDetails.getCapacity());
        existingActivity.setRegistrationDeadline(activityDetails.getRegistrationDeadline());
        existingActivity.setStatus(activityDetails.getStatus());

        return activityRepository.save(existingActivity);
    }

    /**
     * Delete an Activity by ID.
     */
    public void deleteActivity(Long id) {
        Activity activity = getActivityById(id);
        activityRepository.delete(activity);
    }
@Transactional(readOnly = true)
public List<Activity> getActivitiesByOrganizer(Long organizerId) {
    return activityRepository.findByOrganizerId(organizerId);
}
}