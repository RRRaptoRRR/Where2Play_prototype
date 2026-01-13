package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "now_players")
    private Integer nowPlayers = 1;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.active;

    // Связь с участниками (events_to_players)
    @ManyToMany
    @JoinTable(
            name = "events_to_players",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    // Связь с играми (events_to_games)
    @ManyToMany
    @JoinTable(
            name = "events_to_games",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games;

    // Связь с темами (events_to_themes)
    @ManyToMany
    @JoinTable(
            name = "events_to_themes",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes;

    // Связь с правилами (events_to_rules)
    @ManyToMany
    @JoinTable(
            name = "events_to_rules",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private List<Rule> rules;
}
