package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);
    Optional<User> findByGmail(String gmail);
}
