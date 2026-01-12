package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dao.GameDao;
import SamoDev.Where2Play.dao.PlaceDao;
import SamoDev.Where2Play.dao.ThemeDao;
import SamoDev.Where2Play.entity.Game;
import SamoDev.Where2Play.entity.Place;
import SamoDev.Where2Play.entity.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired private PlaceDao placeDao;
    @Autowired private GameDao gameDao;
    @Autowired private ThemeDao themeDao;

    // DTO для ответа (чтобы не тянуть всю сущность с ID и связями)
    record SearchResult(Integer id, String name) {}

    @GetMapping("/places")
    public List<SearchResult> searchPlaces(@RequestParam String query) {
        if (query.length() < 2) return Collections.emptyList();
        return placeDao.findByNameContainingIgnoreCase(query).stream()
                .map(p -> new SearchResult(p.getId(), p.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/games")
    public List<SearchResult> searchGames(@RequestParam String query) {
        if (query.length() < 2) return Collections.emptyList();
        return gameDao.findByNameContainingIgnoreCase(query).stream()
                .map(g -> new SearchResult(g.getId(), g.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/themes")
    public List<SearchResult> searchThemes(@RequestParam String query) {
        if (query.length() < 2) return Collections.emptyList();
        return themeDao.findByNameContainingIgnoreCase(query).stream()
                .map(t -> new SearchResult(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
}
