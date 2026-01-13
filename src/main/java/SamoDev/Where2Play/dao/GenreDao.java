package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreDao extends JpaRepository<Genre, Integer> {

    // Метод для поиска жанра по имени (полезно, чтобы не создавать дубликаты)
    Optional<Genre> findByNameIgnoreCase(String name);

    // Метод для поиска (autocomplete)
    List<Genre> findByNameContainingIgnoreCase(String name);
}
