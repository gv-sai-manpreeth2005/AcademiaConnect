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

@Controller
public class HomeController {

    private final ActivityService activityService;
private final UserRepository userRepository;

    public HomeController(ActivityService activityService,
                      UserRepository userRepository) {

    this.activityService = activityService;
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
}