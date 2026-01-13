package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public String showEvents(Model model, Authentication authentication) {
        List<UpcomingEventSummary> events = eventService.getAllEvents();

        // Создаем карту статусов участия для текущего пользователя
        Map<Integer, Boolean> participationMap = new HashMap<>();
        if (authentication != null) {
            String username = authentication.getName();
            for (UpcomingEventSummary event : events) {
                boolean isRegistered = eventService.isUserRegistered(event.getEventId(), username);
                participationMap.put(event.getEventId(), isRegistered);
            }
            model.addAttribute("username", username); // Для хедера
        }

        model.addAttribute("events", events);
        model.addAttribute("participationMap", participationMap);
        return "events";
    }

    // AJAX endpoint для записи
    @PostMapping("/events/{id}/join")
    @ResponseBody
    public ResponseEntity<?> joinEvent(@PathVariable Integer id, Authentication auth) {
        try {
            eventService.registerOnEvent(id, auth.getName());
            return ResponseEntity.ok().body(Map.of("status", "joined"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // AJAX endpoint для отписки
    @PostMapping("/events/{id}/leave")
    @ResponseBody
    public ResponseEntity<?> leaveEvent(@PathVariable Integer id, Authentication auth) {
        try {
            eventService.unregisterFromEvent(id, auth.getName());
            return ResponseEntity.ok().body(Map.of("status", "left"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
