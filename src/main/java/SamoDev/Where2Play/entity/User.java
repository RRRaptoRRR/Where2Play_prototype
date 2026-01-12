package SamoDev.Where2Play.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "users")
@Getter // Генерирует геттеры
@Setter // Генерирует сеттеры
@NoArgsConstructor // Пустой конструктор (обязателен для Hibernate)
@AllArgsConstructor // Конструктор со всеми полями
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

    @OneToMany(mappedBy = "user")
    private List<PlayerReview> reviews;
}
