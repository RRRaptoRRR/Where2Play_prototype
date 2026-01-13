package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.service.EventService;
import SamoDev.Where2Play.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class CalendarController {

    private final EventService eventService;
    private final UserProfileService userProfileService; // Для получения никнейма в хедере (если нужно)

    @Autowired
    public CalendarController(EventService eventService, UserProfileService userProfileService) {
        this.eventService = eventService;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login"; // Защита от неавторизованного доступа
        }

        String username = authentication.getName();

        // Добавляем никнейм для хедера (как в других контроллерах)
        // Предполагаем, что у вас есть метод получения никнейма, или можно передать просто логин
        // model.addAttribute("username", userProfileService.getCurrentUser(username).getNickname());
        // Если метода нет, пока передадим логин или заглушку
        model.addAttribute("username", username);

        // Получаем данные для календаря из сервиса
        Map<String, List<UpcomingEventSummary>> calendarData = eventService.getCalendarEvents(username);

        model.addAttribute("organizedEvents", calendarData.get("organized"));
        model.addAttribute("participatingEvents", calendarData.get("participating"));
        model.addAttribute("pastEvents", calendarData.get("past"));

        return "calendar";
    }

    @PostMapping("/events/{id}/cancel")
    public String cancelEvent(@PathVariable("id") Integer eventId, Authentication authentication) {
        if (authentication != null) {
            eventService.deleteEvent(eventId, authentication.getName());
        }
        return "redirect:/calendar";
    }
}
