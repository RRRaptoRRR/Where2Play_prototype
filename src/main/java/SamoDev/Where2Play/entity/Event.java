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
    @JoinColumn(name = "organizerid", nullable = false)
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "placeid", nullable = false)
    private Place place;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(name = "maxplayers", nullable = false)
    private Integer maxPlayers;

    @Column(name = "nowplayers")
    private Integer nowPlayers = 1;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.ACTIVE;

    // Связь с участниками (eventstoplayers)
    @ManyToMany
    @JoinTable(
            name = "eventstoplayers",
            joinColumns = @JoinColumn(name = "eventid"),
            inverseJoinColumns = @JoinColumn(name = "userid")
    )
    private List<User> participants;

    // Связь с играми (eventstogames)
    @ManyToMany
    @JoinTable(
            name = "eventstogames",
            joinColumns = @JoinColumn(name = "eventid"),
            inverseJoinColumns = @JoinColumn(name = "gameid")
    )
    private List<Game> games;

    // Связь с темами (eventstothemes)
    @ManyToMany
    @JoinTable(
            name = "eventstothemes",
            joinColumns = @JoinColumn(name = "eventid"),
            inverseJoinColumns = @JoinColumn(name = "themeid")
    )
    private List<Theme> themes;

    // Связь с правилами (eventstorules)
    @ManyToMany
    @JoinTable(
            name = "eventstorules",
            joinColumns = @JoinColumn(name = "eventid"),
            inverseJoinColumns = @JoinColumn(name = "ruleid")
    )
    private List<Rule> rules;
}
