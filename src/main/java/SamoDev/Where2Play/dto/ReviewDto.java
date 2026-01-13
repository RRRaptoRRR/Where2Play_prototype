package SamoDev.Where2Play.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private String text;
    private String type; // "PLAYER" или "ORGANIZER"
}
