package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.OrganizerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerReviewDao extends JpaRepository<OrganizerReview, Integer> {
    List<OrganizerReview> findByOrganizerIdOrderByIdDesc(Integer organizerId);
}
