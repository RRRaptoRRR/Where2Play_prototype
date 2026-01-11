package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizer_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @Column(nullable = false)
    private String review;
}
