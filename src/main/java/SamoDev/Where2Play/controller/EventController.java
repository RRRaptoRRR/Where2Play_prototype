package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ИЗМЕНИЛИ МАППИНГ С "/" НА "/events"
    @GetMapping("/events")
    public String showEvents(Model model) {
        List<UpcomingEventSummary> events = eventService.getUpcomingEvents();
        model.addAttribute("events", events);
        return "events";
    }
}
