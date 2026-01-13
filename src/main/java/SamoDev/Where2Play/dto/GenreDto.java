package SamoDev.Where2Play.dto;

import lombok.Data;

@Data
public class GenreDto {
    private Integer id;
    private String name;
    private String description;
    private boolean isNew; // Флаг: true - создаем новый, false - выбрали существующий
}
