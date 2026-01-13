package SamoDev.Where2Play.dto;

import java.time.LocalDateTime;

public interface UpcomingEventSummary {
    Integer getEventId(); // Добавили ID! (В SQL запросе должно быть 'e.id AS event_id')
    String getEventTitle();
    String getOrganizerName();
    LocalDateTime getEventDate();
    String getEventDescription();
    String getTopicsList();
    String getRulesList();
    String getCity();
    String getDistrict();
    String getAddress();
    String getGamesList();
    Integer getCurrentPlayers();
    Integer getMaxPlayers();
    String getStatus();
    String getRoleName();
}
