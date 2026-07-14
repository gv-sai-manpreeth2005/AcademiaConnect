package org.cse.academiaconnect.controller;

import org.cse.academiaconnect.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.cse.academiaconnect.entity.Activity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;
import org.cse.academiaconnect.entity.User;
import org.cse.academiaconnect.repository.UserRepository;
import org.cse.academiaconnect.dto.CreateActivityRequest;
import org.cse.academiaconnect.service.RegistrationService;
import org.cse.academiaconnect.entity.Registration;
import org.cse.academiaconnect.entity.Waitlist;
import org.cse.academiaconnect.service.WaitlistService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.cse.academiaconnect.entity.Feedback;
import org.cse.academiaconnect.service.FeedbackService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    private final ActivityService activityService;
    private final RegistrationService registrationService;
    private final WaitlistService waitlistService;
    private final FeedbackService feedbackService;
private final UserRepository userRepository;

  public HomeController(
        ActivityService activityService,
        RegistrationService registrationService,
        WaitlistService waitlistService,
        FeedbackService feedbackService,
        UserRepository userRepository) {

    this.activityService = activityService;
    this.registrationService = registrationService;
    this.waitlistService = waitlistService;
    this.feedbackService = feedbackService;
    this.userRepository = userRepository;
}

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/activities")
    public String activities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        return "activities";
    }

@GetMapping("/activities/{id}")
public String activityDetails(@PathVariable Long id,
                              Model model) {

    model.addAttribute("activity", activityService.getActivityById(id));

    return "activity-details";
}

    @GetMapping("/dashboard")
public String userDashboard() {
    return "dashboard";
}
    @GetMapping("/organizer")
    public String organizerDashboard() {
        return "dashboard-organizer";
    }
@GetMapping("/activities/create")
public String createActivityPage(Model model) {

    model.addAttribute("activity", new CreateActivityRequest());

    return "create-activity";
}



    @GetMapping("/certificates")
    public String certificates() {
        return "certificates";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "feedback";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

   @PostMapping("/activities/create")
public String createActivity(
        @Valid @ModelAttribute("activity") CreateActivityRequest request,
        Principal principal) {

    User organizer = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    Activity activity = new Activity();

    activity.setTitle(request.getTitle());
    activity.setDescription(request.getDescription());
    activity.setLocation(request.getLocation());
    activity.setCapacity(request.getCapacity());
    activity.setDateTime(request.getDateTime());
    activity.setRegistrationDeadline(request.getRegistrationDeadline());
    activity.setType(Activity.ActivityType.valueOf(request.getType()));
    activity.setStatus(Activity.ActivityStatus.valueOf(request.getStatus()));
    activity.setOrganizer(organizer);

    activityService.createActivity(activity);

    return "redirect:/activities";
}



@PostMapping("/activities/register/{id}")
public String registerForActivity(
        @PathVariable Long id,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    Activity activity = activityService.getActivityById(id);

    if (registrationService.isUserRegistered(user.getId(), id)) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You are already registered for this activity."
        );

        return "redirect:/activities/" + id;
    }

    if (registrationService.isActivityFull(id)) {
        if (waitlistService.isUserWaitlisted(user.getId(), id)) {

    redirectAttributes.addFlashAttribute(
            "errorMessage",
            "You are already on the waitlist for this activity."
    );

    return "redirect:/activities/" + id;
}

        Waitlist waitlist = new Waitlist();
        waitlist.setUser(user);
        waitlist.setActivity(activity);

        Waitlist savedWaitlist =
                waitlistService.createWaitlist(waitlist);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Activity is full. You were added to the waitlist at position "
                        + savedWaitlist.getQueuePosition() + "."
        );

        return "redirect:/activities/" + id;
    }

    Registration registration = new Registration();
    registration.setUser(user);
    registration.setActivity(activity);

    registrationService.createRegistration(registration);

    redirectAttributes.addFlashAttribute(
            "successMessage",
            "You have successfully registered for this activity."
    );

    return "redirect:/activities/" + id;
}

@GetMapping("/my-registrations")
public String myRegistrations(Model model,
                              Principal principal) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    model.addAttribute(
            "registrations",
            registrationService.getRegistrationsByUser(user.getId())
    );

    return "my-registrations";
}

@GetMapping("/my-waitlist")
public String myWaitlist(Model model, Principal principal) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    model.addAttribute(
            "waitlistEntries",
            waitlistService.getWaitlistEntriesByUser(user.getId())
    );

    return "my-waitlist";
}
@PostMapping("/my-waitlist/cancel/{id}")
public String cancelWaitlist(
        @PathVariable Long id,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    waitlistService.cancelWaitlist(id, user.getId());

    redirectAttributes.addFlashAttribute(
            "successMessage",
            "Your waitlist entry has been cancelled."
    );

    return "redirect:/my-waitlist";
}

@PostMapping("/my-registrations/cancel/{id}")
public String cancelRegistration(
        @PathVariable Long id,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    registrationService.cancelRegistration(id, user.getId());

    redirectAttributes.addFlashAttribute(
            "successMessage",
            "Registration cancelled successfully."
    );

    return "redirect:/my-registrations";
}

@GetMapping("/organizer/activities")
public String organizerActivities(Model model, Principal principal) {

    User organizer = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    model.addAttribute(
            "activities",
            activityService.getActivitiesByOrganizer(organizer.getId())
    );

    return "organizer-activities";
}
@GetMapping("/organizer/activities/{id}/participants")
public String viewParticipants(
        @PathVariable Long id,
        Model model,
        Principal principal) {

    User organizer = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    Activity activity = activityService.getActivityById(id);

    if (!activity.getOrganizer().getId().equals(organizer.getId())) {
        throw new IllegalArgumentException(
                "You are not allowed to view participants for this activity."
        );
    }

    model.addAttribute("activity", activity);

    model.addAttribute(
            "registrations",
            registrationService.getRegistrationsByActivity(id)
    );

    model.addAttribute(
            "waitlistEntries",
            waitlistService.getWaitlistEntriesByActivity(id)
    );

    return "organizer-participants";
}

@GetMapping("/activities/{id}/feedback")
public String feedbackForm(
        @PathVariable Long id,
        Model model,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    if (!registrationService.isUserRegistered(user.getId(), id)) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You must register for this activity before submitting feedback."
        );

        return "redirect:/my-registrations";
    }

    if (feedbackService.hasUserSubmittedFeedback(user.getId(), id)) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You have already submitted feedback for this activity."
        );

        return "redirect:/my-registrations";
    }

    model.addAttribute("activity", activityService.getActivityById(id));

    return "feedback-form";
}

@PostMapping("/activities/{id}/feedback")
public String submitFeedback(
        @PathVariable Long id,
        @RequestParam Integer rating,
        @RequestParam(required = false) String comments,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    User user = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    Activity activity = activityService.getActivityById(id);

    if (!registrationService.isUserRegistered(user.getId(), id)) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You are not registered for this activity."
        );

        return "redirect:/my-registrations";
    }

    if (feedbackService.hasUserSubmittedFeedback(user.getId(), id)) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "You have already submitted feedback."
        );

        return "redirect:/my-registrations";
    }

    if (rating < 1 || rating > 5) {
        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Rating must be between 1 and 5."
        );

        return "redirect:/activities/" + id + "/feedback";
    }

    Feedback feedback = new Feedback();
    feedback.setUser(user);
    feedback.setActivity(activity);
    feedback.setRating(rating);
    feedback.setComments(comments);

    feedbackService.createFeedback(feedback);

    redirectAttributes.addFlashAttribute(
            "successMessage",
            "Feedback submitted successfully."
    );

    return "redirect:/my-registrations";
}

@GetMapping("/organizer/activities/{id}/feedback")
public String organizerFeedback(
        @PathVariable Long id,
        Model model,
        Principal principal) {

    User organizer = userRepository.findByUsername(principal.getName())
            .orElseThrow();

    Activity activity = activityService.getActivityById(id);

    if (!activity.getOrganizer().getId().equals(organizer.getId())) {
        throw new IllegalArgumentException(
                "You are not allowed to view feedback for this activity."
        );
    }

    model.addAttribute("activity", activity);
    model.addAttribute(
            "feedbacks",
            feedbackService.getFeedbacksByActivity(id)
    );
    model.addAttribute(
            "averageRating",
            feedbackService.getAverageRating(id)
    );

    return "organizer-feedback";
}

}