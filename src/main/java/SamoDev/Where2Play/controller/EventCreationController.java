package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.EventCreationDto;
import SamoDev.Where2Play.service.EventCreationService; // Реализуем позже
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/create")
public class EventCreationController {

    @Autowired
    private EventCreationService eventCreationService;

    @GetMapping
    public String showCreateForm() {
        return "create-event";
    }

    @PostMapping
    @ResponseBody // Отвечаем JSON-ом (успех/ошибка)
    public ResponseEntity<?> createEvent(@RequestBody EventCreationDto dto) {
        try {
            eventCreationService.createEvent(dto);
            return ResponseEntity.ok().body("Событие успешно создано");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // Методы для поиска (API) - чтобы JS мог искать игры/места
    // Реализацию сервисов поиска опустим для краткости, они тривиальны (findByTitleLike)
}
