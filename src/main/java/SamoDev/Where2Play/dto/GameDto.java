package SamoDev.Where2Play.dto;

import SamoDev.Where2Play.entity.Difficulty;
import lombok.Data;

import java.util.List;

@Data
public class GameDto {
    private String name;
    private String description;
    private Integer maxPlayers;
    private Difficulty difficulty; // Низкий, Средний, Высокий
    private Integer time; // минуты
    private List<GenreDto> newGenres; // Можно сразу создавать и жанры
    private List<Integer> genreIds;   // Или выбирать
}
