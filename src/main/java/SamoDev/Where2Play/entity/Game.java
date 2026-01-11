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

    @Column(name = "maxplayers")
    private Integer maxPlayers;

    private String difficulty; // Можно сделать Enum, если значения фиксированы
    private Integer time;

    @ManyToMany
    @JoinTable(
            name = "gamestogenre",
            joinColumns = @JoinColumn(name = "gameid"),
            inverseJoinColumns = @JoinColumn(name = "genreid")
    )
    private List<Genre> genres;
}
