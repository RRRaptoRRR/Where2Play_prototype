package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ThemeDao extends JpaRepository<Theme, Integer> {
    List<Theme> findByNameContainingIgnoreCase(String name);
}
