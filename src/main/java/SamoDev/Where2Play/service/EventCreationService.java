package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.*;
import SamoDev.Where2Play.dto.*;
import SamoDev.Where2Play.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventCreationService {

    @Autowired private EventDao eventDao;
    @Autowired private PlaceDao placeDao; // Нужно создать (см. ниже)
    @Autowired private GameDao gameDao;   // Нужно создать
    @Autowired private ThemeDao themeDao; // Нужно создать
    @Autowired private RuleDao ruleDao;   // Нужно создать
    @Autowired private UserDao userDao;
    @Autowired private OrganizerDao organizerDao; // Нужно создать

    @Transactional
    public void createEvent(EventCreationDto dto) {
        System.out.println("Start creating event: " + dto);
        // 1. Определяем Организатора (текущий пользователь)
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findByLogin(currentLogin)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем, есть ли у пользователя профиль организатора, если нет - создаем
        Organizer organizer = organizerDao.findByUser(user)
                .orElseGet(() -> {
                    Organizer newOrg = new Organizer();
                    newOrg.setUser(user);
                    return organizerDao.save(newOrg);
                });

        // 2. Определяем Место
        Place place;
        if (dto.getNewPlace() != null) {
            // Создаем новое место
            place = new Place();
            place.setName(dto.getNewPlace().getName());
            place.setCity(dto.getNewPlace().getCity());
            place.setDistrict(dto.getNewPlace().getDistrict());
            place.setAddress(dto.getNewPlace().getAddress());
            place.setDescription(dto.getNewPlace().getDescription());
            place.setAdditionalInfo(dto.getNewPlace().getAdditionalInfo());
            place = placeDao.save(place);
        } else if (dto.getPlaceId() != null) {
            // Ищем существующее
            place = placeDao.findById(dto.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("Место не найдено"));
        } else {
            throw new RuntimeException("Место проведения не указано");
        }

        // 3. Собираем списки Игр, Тем, Правил
        List<Game> games = new ArrayList<>();
        // Существующие игры
        if (dto.getGameIds() != null) {
            games.addAll(gameDao.findAllById(dto.getGameIds()));
        }
        // Новые игры
        if (dto.getNewGames() != null) {
            for (GameDto gameDto : dto.getNewGames()) {
                Game newGame = new Game();
                newGame.setName(gameDto.getName());
                newGame.setDifficulty(gameDto.getDifficulty());
                newGame.setDescription(gameDto.getDescription());
                // Можно добавить жанры и т.д.
                games.add(gameDao.save(newGame));
            }
        }

        List<Theme> themes = new ArrayList<>();
        if (dto.getThemeIds() != null) themes.addAll(themeDao.findAllById(dto.getThemeIds()));
        if (dto.getNewThemes() != null) {
            for (ThemeDto themeDto : dto.getNewThemes()) {
                Theme newTheme = new Theme();
                newTheme.setName(themeDto.getName());
                themes.add(themeDao.save(newTheme));
            }
        }

        // 4. Создаем Событие
        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setData(dto.getDate());
        event.setMaxPlayers(dto.getMaxPlayers());
        event.setNowPlayers(0); // Изначально 0 (или 1, если орг играет)
        event.setStatus(EventStatus.active);

        event.setOrganizer(organizer);
        event.setPlace(place);

        event.setGames(games);
        event.setThemes(themes);
        // event.setRules(...) - аналогично

        System.out.println("Saving event...");
        eventDao.save(event);
    }
}
