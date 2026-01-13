package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.dto.ParticipantDto;
import SamoDev.Where2Play.dto.ParticipantSummary;
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

    @Query(value = """
        SELECT u.id as userId, u.name as name
        FROM users u 
        JOIN organizers o ON u.id = o.user_id 
        JOIN events e ON o.id = e.organizer_id
        WHERE e.id = :eventId
    """, nativeQuery = true)
    ParticipantSummary findOrganizerSummary(@Param("eventId") Integer eventId);

    @Query(value = """
        SELECT u.id as userId, u.name as name
        FROM users u 
        JOIN events_to_players etp ON u.id = etp.user_id 
        WHERE etp.event_id = :eventId
        ORDER BY etp.id ASC  -- Сортировка по порядку добавления
    """, nativeQuery = true)
    List<ParticipantSummary> findParticipantsSummary(@Param("eventId") Integer eventId);


    // Чтобы получить название ивента для заголовка
    @Query("SELECT e.name FROM Event e WHERE e.id = :eventId")
    String findEventNameById(@Param("eventId") Integer eventId);

    // 1. Мероприятия, где я организатор (и активные)
    @Query(value = """
        SELECT * FROM get_upcoming_events() 
        WHERE event_id IN (
            SELECT e.id FROM events e 
            JOIN organizers o ON e.organizer_id = o.id 
            WHERE o.user_id = :userId AND e.status = 'active'
        )
    """, nativeQuery = true)
    List<UpcomingEventSummary> findMyOrganizedEvents(@Param("userId") Integer userId);

    // 2. Мероприятия, куда я записан как игрок (но не организатор)
    @Query(value = """
        SELECT * FROM get_upcoming_events()
        WHERE event_id IN (
            SELECT etp.event_id FROM events_to_players etp
            WHERE etp.user_id = :userId
        )
        AND event_id NOT IN (
            SELECT e.id FROM events e 
            JOIN organizers o ON e.organizer_id = o.id 
            WHERE o.user_id = :userId
        )
    """, nativeQuery = true)
    List<UpcomingEventSummary> findMyParticipatingEvents(@Param("userId") Integer userId);

    @Query(value = """
        SELECT 
            e.id AS event_id,
            e.name AS event_title,
            u.name AS organizer_name,
            e.data AS event_date,
            e.description AS event_description,
            e.status AS status,   -- Добавили статус
            CASE WHEN o.user_id = :userId THEN 'Организатор' ELSE 'Участник' END AS role_name, -- Добавили роль
            -- остальные поля как в get_upcoming_events
            COALESCE(STRING_AGG(DISTINCT t.name, ', '), 'Нет тем') AS topics_list,
            COALESCE(STRING_AGG(DISTINCT r.description, '; '), 'Нет особых правил') AS rules_list,
            p.city, p.district, p.address,
            COALESCE(STRING_AGG(DISTINCT g.name, ', '), 'Игры не указаны') AS games_list,
            e.now_players AS current_players,
            e.max_players AS max_players
        FROM events e
        JOIN organizers o ON e.organizer_id = o.id
        JOIN users u ON o.user_id = u.id
        JOIN places p ON e.place_id = p.id
        LEFT JOIN events_to_themes ett ON e.id = ett.event_id
        LEFT JOIN themes t ON ett.theme_id = t.id
        LEFT JOIN events_to_rules etr ON e.id = etr.event_id
        LEFT JOIN rules r ON etr.rule_id = r.id
        LEFT JOIN events_to_games etg ON e.id = etg.event_id
        LEFT JOIN games g ON etg.game_id = g.id
        LEFT JOIN events_to_players etp ON e.id = etp.event_id
        WHERE (etp.user_id = :userId OR o.user_id = :userId)
        AND (e.status IN ('completed', 'cancelled') OR e.data < NOW())
        GROUP BY e.id, e.name, u.name, e.data, e.description, p.city, p.district, p.address, e.now_players, e.max_players, e.status, o.user_id
        ORDER BY e.data DESC
    """, nativeQuery = true)
    List<UpcomingEventSummary> findMyPastEvents(@Param("userId") Integer userId);

    @Modifying
    @Query(value = "INSERT INTO events_to_players(event_id, user_id) VALUES (:eventId, :userId)", nativeQuery = true)
    void addParticipant(@Param("eventId") Integer eventId, @Param("userId") Integer userId);




}
