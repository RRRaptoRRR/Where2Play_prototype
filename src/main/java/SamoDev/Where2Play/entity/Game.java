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

    @Enumerated(EnumType.STRING) // [web:118][web:120]
    private Difficulty difficulty;

    private Integer time;

    @ManyToMany
    @JoinTable(
            name = "games_to_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}
