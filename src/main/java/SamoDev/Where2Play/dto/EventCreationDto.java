package SamoDev.Where2Play.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreationDto {
    private String name;
    private String description;
    private LocalDateTime date;
    private Integer maxPlayers;

    // ID выбранного места (если выбрано из списка)
    private Integer placeId;
    // Новое место (если создаем)
    private PlaceDto newPlace;

    // Списки ID выбранных сущностей
    private List<Integer> gameIds;
    private List<Integer> themeIds;
    private List<Integer> ruleIds;

    // Списки новых сущностей для создания
    private List<GameDto> newGames;
    private List<ThemeDto> newThemes;
    private List<RuleDto> newRules;
}
