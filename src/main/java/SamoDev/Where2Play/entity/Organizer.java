package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "organizers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Связь 1:N с отзывами об организаторе
    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    private List<OrganizerReview> reviews;
}
