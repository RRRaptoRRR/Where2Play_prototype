package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.EventDao;
import SamoDev.Where2Play.dto.UpcomingEventSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventDao eventDao;

    @Autowired
    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Transactional(readOnly = true)
    public List<UpcomingEventSummary> getUpcomingEvents() {
        return eventDao.findUpcomingEvents();
    }
}
