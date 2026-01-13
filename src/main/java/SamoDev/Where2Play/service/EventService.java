package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.EventDao;
import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.dto.ParticipantSummary;
import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDao eventDao;
    private final UserDao userDao;

    @Transactional(readOnly = true)
    public List<UpcomingEventSummary> getAllEvents() {
        return eventDao.findUpcomingEvents();
    }

    @Transactional
    public void registerOnEvent(Integer eventId, String username) {
        User user = getUserByUsername(username);
        // Проверка: организатор не может записываться кнопкой (он уже там)
        if (eventDao.isUserOrganizer(eventId, user.getId())) {
            throw new RuntimeException("Вы организатор этого события");
        }
        eventDao.registerPlayer(eventId, user.getId());
    }

    @Transactional
    public void unregisterFromEvent(Integer eventId, String username) {
        User user = getUserByUsername(username);
        eventDao.unregisterPlayer(eventId, user.getId());
    }

    @Transactional(readOnly = true)
    public boolean isUserRegistered(Integer eventId, String username) {
        if (username == null) return false;
        User user = getUserByUsername(username);
        return eventDao.isUserRegistered(eventId, user.getId());
    }

    private User getUserByUsername(String username) {
        return userDao.findByLogin(username)
                .or(() -> userDao.findByGmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public ParticipantSummary getEventOrganizer(Integer eventId) {
        return eventDao.findOrganizerSummary(eventId);
    }

    @Transactional(readOnly = true)
    public List<ParticipantSummary> getEventParticipants(Integer eventId) {
        // Получаем всех участников
        List<ParticipantSummary> participants = eventDao.findParticipantsSummary(eventId);

        // Если нужно исключить организатора из общей таблицы (чтобы не дублировался),
        // можно отфильтровать здесь. Но обычно показывают всех.
        return participants;
    }

    @Transactional(readOnly = true)
    public String getEventName(Integer eventId) {
        return eventDao.findEventNameById(eventId);
    }

}
