package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.PlayerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerReviewDao extends JpaRepository<PlayerReview, Integer> {
    List<PlayerReview> findByUserIdOrderByIdDesc(Integer userId);
}
