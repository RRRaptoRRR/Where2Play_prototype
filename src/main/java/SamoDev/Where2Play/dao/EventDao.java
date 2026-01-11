package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDao extends JpaRepository<Event, Integer> {

    @Query(value = "SELECT * FROM get_upcoming_events()", nativeQuery = true)
    List<UpcomingEventSummary> findUpcomingEvents();
}
