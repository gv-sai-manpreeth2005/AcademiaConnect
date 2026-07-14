package org.cse.academiaconnect.service;

import jakarta.persistence.EntityNotFoundException;
import org.cse.academiaconnect.entity.Activity;
import org.cse.academiaconnect.entity.Registration;
import org.cse.academiaconnect.repository.ActivityRepository;
import org.cse.academiaconnect.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class managing Registration business logic.
 * Uses constructor injection and handles clean CRUD operations.
 */
@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;

    public RegistrationService(RegistrationRepository registrationRepository, ActivityRepository activityRepository) {
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Create a Registration.
     * Verifies registration deadlines, duplicates, and activity capacity.
     */
    public Registration createRegistration(Registration registration) {
        Activity activity = activityRepository.findById(registration.getActivity().getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with ID: " + registration.getActivity().getId()));

        if (LocalDateTime.now().isAfter(activity.getRegistrationDeadline())) {
            throw new IllegalStateException("Registration deadline has passed for this activity");
        }

        if (registrationRepository.existsByUserIdAndActivityId(registration.getUser().getId(), activity.getId())) {
            throw new IllegalArgumentException("User is already registered for this activity");
        }

        long activeRegistrations = registrationRepository.countByActivityIdAndStatus(activity.getId(), Registration.RegistrationStatus.REGISTERED);
        if (activeRegistrations >= activity.getCapacity()) {
            throw new IllegalStateException("Activity capacity has been reached. Please join the waitlist instead.");
        }

        registration.setStatus(Registration.RegistrationStatus.REGISTERED);
        return registrationRepository.save(registration);
    }

    /**
     * Retrieve all Registrations.
     */
    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    /**
     * Retrieve a Registration by ID.
     */
    @Transactional(readOnly = true)
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with ID: " + id));
    }

    /**
     * Update an existing Registration.
     */
    public Registration updateRegistration(Long id, Registration registrationDetails) {
        Registration existingRegistration = getRegistrationById(id);
        existingRegistration.setStatus(registrationDetails.getStatus());
        return registrationRepository.save(existingRegistration);
    }

    /**
     * Delete a Registration by ID.
     */
    public void deleteRegistration(Long id) {
        Registration registration = getRegistrationById(id);
        registrationRepository.delete(registration);
    }
}