package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "max_players")
    private Integer maxPlayers;

    private String difficulty; // Можно сделать Enum, если значения фиксированы
    private Integer time;

    @ManyToMany
    @JoinTable(
            name = "games_to_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}
