package org.cse.academiaconnect.controller;

import org.cse.academiaconnect.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    private final ActivityService activityService;

    public HomeController(ActivityService activityService) {
        this.activityService = activityService;
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
}