package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playerreview")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(nullable = false)
    private String review;
}
