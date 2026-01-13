package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String gmail;

    private String phone;

    private String nickname;

    // Связь 1:N с отзывами об игроке
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PlayerReview> playerReviews;

    // Связь 1:1 с организатором (обратная сторона)
    // Это нужно, чтобы в контроллере легко проверить if (user.getOrganizer() != null)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Organizer organizer;
}
