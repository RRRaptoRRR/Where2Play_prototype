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
import java.util.Optional;

@Service
public class EventCreationService {

    @Autowired private EventDao eventDao;
    @Autowired private PlaceDao placeDao;
    @Autowired private GameDao gameDao;
    @Autowired private ThemeDao themeDao;
    @Autowired private RuleDao ruleDao;
    @Autowired private UserDao userDao;
    @Autowired private OrganizerDao organizerDao;
    @Autowired private GenreDao genreDao;

    @Transactional
    public void createEvent(EventCreationDto dto) {
        System.out.println("Creating event: " + dto.getName());

        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findByLogin(currentLogin)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + currentLogin));

        Organizer organizer = organizerDao.findByUser(user)
                .orElseGet(() -> {
                    Organizer newOrg = new Organizer();
                    newOrg.setUser(user);
                    return organizerDao.save(newOrg);
                });

        // 2. МЕСТО
        Place place;
        if (dto.getNewPlace() != null) {
            place = new Place();
            place.setName(dto.getNewPlace().getName());
            place.setCity(dto.getNewPlace().getCity());
            place.setDistrict(dto.getNewPlace().getDistrict());
            place.setAddress(dto.getNewPlace().getAddress());
            place.setDescription(dto.getNewPlace().getDescription());
            place.setAdditionalInfo(dto.getNewPlace().getAdditionalInfo());
            place = placeDao.save(place);
        } else if (dto.getPlaceId() != null) {
            place = placeDao.findById(dto.getPlaceId())
                    .orElseThrow(() -> new RuntimeException("Место с ID " + dto.getPlaceId() + " не найдено"));
        } else {
            throw new RuntimeException("Место проведения не указано");
        }

        // 3. ИГРЫ
        List<Game> games = new ArrayList<>();
        // 3.1 Существующие
        if (dto.getGameIds() != null && !dto.getGameIds().isEmpty()) {
            games.addAll(gameDao.findAllById(dto.getGameIds()));
        }
        // 3.2 Новые
        if (dto.getNewGames() != null) {
            for (GameDto gDto : dto.getNewGames()) {
                Game newGame = new Game();
                newGame.setName(gDto.getName());
                newGame.setDescription(gDto.getDescription());
                newGame.setMaxPlayers(gDto.getMaxPlayers());
                newGame.setTime(gDto.getTime());

                // Сложность
                if (gDto.getDifficulty() != null) {
                    try {
                        // Используем toUpperCase() если в Java ENUM большие буквы (EASY, HARD)
                        // Если маленькие (easy, hard) - уберите toUpperCase()
                        newGame.setDifficulty(Difficulty.valueOf(gDto.getDifficulty().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        newGame.setDifficulty(null);
                    }
                }

                // ЖАНРЫ (Внутри новой игры)
                if (gDto.getGenres() != null && !gDto.getGenres().isEmpty()) {
                    List<Genre> gameGenres = new ArrayList<>();
                    for (GenreDto genreDto : gDto.getGenres()) {
                        if (genreDto.isNew()) {
                            // Создаем новый, проверяя дубликаты по имени
                            Optional<Genre> existing = genreDao.findByNameIgnoreCase(genreDto.getName());
                            if (existing.isPresent()) {
                                gameGenres.add(existing.get());
                            } else {
                                Genre newG = new Genre();
                                newG.setName(genreDto.getName());
                                newG.setDescription(genreDto.getDescription());
                                gameGenres.add(genreDao.save(newG));
                            }
                        } else {
                            // Существующий: проверяем ID
                            if (genreDto.getId() != null) {
                                genreDao.findById(genreDto.getId()).ifPresent(gameGenres::add);
                            } else {
                                // Если ID нет, пробуем по имени
                                genreDao.findByNameIgnoreCase(genreDto.getName()).ifPresent(gameGenres::add);
                            }
                        }
                    }
                    newGame.setGenres(gameGenres);
                }
                games.add(gameDao.save(newGame));
            }
        }

        // 4. ТЕМЫ
        List<Theme> themes = new ArrayList<>();
        if (dto.getThemeIds() != null) themes.addAll(themeDao.findAllById(dto.getThemeIds()));
        if (dto.getNewThemes() != null) {
            for (ThemeDto tDto : dto.getNewThemes()) {
                Theme newTheme = new Theme();
                newTheme.setName(tDto.getName());
                newTheme.setDescription(tDto.getDescription());
                themes.add(themeDao.save(newTheme));
            }
        }

        // 5. ПРАВИЛА
        List<Rule> rules = new ArrayList<>();
        // Существующие правила
        if (dto.getRuleIds() != null && !dto.getRuleIds().isEmpty()) {
            rules.addAll(ruleDao.findAllById(dto.getRuleIds()));
        }
        // Новые правила
        if (dto.getNewRules() != null) {
            for (RuleDto rDto : dto.getNewRules()) {
                Rule r = new Rule();
                r.setDescription(rDto.getDescription());
                rules.add(ruleDao.save(r));
            }
        }

        // 6. СОЗДАНИЕ СОБЫТИЯ
        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setData(dto.getDate());
        event.setMaxPlayers(dto.getMaxPlayers());
        event.setNowPlayers(1);

        // ВАЖНО: Убедитесь, что EventStatus совпадает с БД (обычно lowercase)
        event.setStatus(EventStatus.active);

        event.setOrganizer(organizer);
        event.setPlace(place);
        event.setGames(games);
        event.setThemes(themes);
        event.setRules(rules);

        eventDao.save(event);
        System.out.println("Event saved successfully: " + event.getId());
    }
}
