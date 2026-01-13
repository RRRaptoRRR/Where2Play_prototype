package SamoDev.Where2Play.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
    private Integer userId;
    private String name; // ФИО
    // Можно добавить логин или никнейм, если понадобится для ссылки
}
