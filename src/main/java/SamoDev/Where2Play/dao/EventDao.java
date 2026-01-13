package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.entity.Event; // Убедитесь, что Entity Event существует
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDao extends JpaRepository<Event, Integer> {

    // Важно: в SQL функции должно возвращаться поле id как event_id,
    // либо измените геттер в интерфейсе на getId()
    @Query(value = "SELECT * FROM get_upcoming_events()", nativeQuery = true)
    List<UpcomingEventSummary> findUpcomingEvents();

    @Modifying
    @Query(value = "CALL register_player_on_event(:eventId, :userId)", nativeQuery = true)
    void registerPlayer(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    @Modifying
    @Query(value = "CALL unregister_player_from_event(:eventId, :userId)", nativeQuery = true)
    void unregisterPlayer(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    @Query(value = "SELECT COUNT(*) > 0 FROM events_to_players WHERE event_id = :eventId AND user_id = :userId", nativeQuery = true)
    boolean isUserRegistered(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    // Проверка, является ли пользователь организатором
    @Query(value = "SELECT COUNT(*) > 0 FROM events e JOIN organizers o ON e.organizer_id = o.id WHERE e.id = :eventId AND o.user_id = :userId", nativeQuery = true)
    boolean isUserOrganizer(@Param("eventId") Integer eventId, @Param("userId") Integer userId);
}
