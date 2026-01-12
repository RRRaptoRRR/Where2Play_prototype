package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaceDao extends JpaRepository<Place, Integer> {
    // Поиск для автодополнения (игнорируя регистр)
    List<Place> findByNameContainingIgnoreCase(String name);
}
