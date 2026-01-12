package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameDao extends JpaRepository<Game, Integer> {
    List<Game> findByNameContainingIgnoreCase(String name);
}
