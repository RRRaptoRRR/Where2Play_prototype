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
    @JoinColumn(name = "userid", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "organizer")
    private List<OrganizerReview> reviews;
}
