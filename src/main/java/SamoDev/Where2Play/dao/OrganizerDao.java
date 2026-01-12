package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Organizer;
import SamoDev.Where2Play.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizerDao extends JpaRepository<Organizer, Integer> {
    Optional<Organizer> findByUser(User user);
}
