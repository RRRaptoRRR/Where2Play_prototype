package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.EventDao;
import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.dto.ParticipantSummary;
import SamoDev.Where2Play.dto.UpcomingEventSummary;
import SamoDev.Where2Play.entity.Event;
import SamoDev.Where2Play.entity.EventStatus;
import SamoDev.Where2Play.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDao eventDao;
    private final UserDao userDao;

    @Transactional(readOnly = true)
    public List<UpcomingEventSummary> getAllEvents(String currentUsername) {
        // 1. Получаем все события из базы
        List<UpcomingEventSummary> allEvents = eventDao.findUpcomingEvents();

        // 2. Если пользователь не авторизован, показываем всё
        if (currentUsername == null) {
            return allEvents;
        }

        // 3. Находим ID текущего пользователя
        User currentUser = userDao.findByLogin(currentUsername)
                .or(() -> userDao.findByGmail(currentUsername))
                .orElse(null);

        if (currentUser == null) return allEvents;

        final Integer currentUserId = currentUser.getId();

        // 4. Фильтруем: оставляем только те события, где текущий юзер НЕ организатор
        // (Метод isUserOrganizer мы добавляли в EventDao ранее)
        return allEvents.stream()
                .filter(event -> !eventDao.isUserOrganizer(event.getEventId(), currentUserId))
                .collect(Collectors.toList());
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

    @Transactional(readOnly = true)
    public Map<String, List<UpcomingEventSummary>> getCalendarEvents(String username) {
        User user = userDao.findByLogin(username)
                .or(() -> userDao.findByGmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer userId = user.getId();

        Map<String, List<UpcomingEventSummary>> map = new HashMap<>();
        map.put("organized", eventDao.findMyOrganizedEvents(userId));
        map.put("participating", eventDao.findMyParticipatingEvents(userId));
        map.put("past", eventDao.findMyPastEvents(userId));

        return map;
    }

    @Transactional
    public void deleteEvent(Integer eventId, String username) {
        // Проверка прав (является ли организатором)
        // Логика удаления (помечаем как cancelled или удаляем из БД)
        // eventDao.deleteById(eventId);
        // Или лучше:

        Event event = eventDao.findById(eventId).orElseThrow();
        // check owner...
        event.setStatus(EventStatus.cancelled);
        eventDao.save(event);
    }

    @Transactional
    public void createEvent(SamoDev.Where2Play.entity.Event event, String username) {
        // 1. Ищем пользователя-организатора
        User user = getUserByUsername(username);

        // Проверяем, есть ли у него профиль организатора
        if (user.getOrganizer() == null) {
            throw new RuntimeException("Пользователь не является организатором");
        }

        // 2. Настраиваем событие
        event.setOrganizer(user.getOrganizer());
        event.setNowPlayers(0); // Явно ставим 0, чтобы триггер потом сделал 1
        event.setStatus(EventStatus.active); // Или EventStatus.active если enum

        // 3. Сохраняем событие
        Event savedEvent = eventDao.save(event);

        // 4. Явно записываем организатора как участника
        // (Используем нативный запрос или процедуру, чтобы триггер обновления счетчика сработал)
        eventDao.addParticipant(savedEvent.getId(), user.getId());
    }



}
