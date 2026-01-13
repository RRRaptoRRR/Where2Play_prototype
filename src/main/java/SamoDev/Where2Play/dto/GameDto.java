package SamoDev.Where2Play.dto;

import lombok.Data;
import java.util.List;

@Data
public class GameDto {
    private String name;
    private String description;
    private Integer maxPlayers;
    private Integer time;       // Время партии в минутах
    private String difficulty;

    // Вот этого поля не хватает:
    private List<GenreDto> genres;
}
