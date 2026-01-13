package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.dto.UserUpdateDto;
import SamoDev.Where2Play.entity.OrganizerReview;
import SamoDev.Where2Play.entity.PlayerReview;
import SamoDev.Where2Play.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserDao userDao;

    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        return userDao.findByGmail(username)
                .or(() -> userDao.findByLogin(username))
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional
    public void updateUserProfile(String currentUsername, UserUpdateDto dto) {
        User user = getCurrentUser(currentUsername);

        // Обновляем поля, если они пришли
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user.setPhone(dto.getPhone());
        }

        userDao.save(user);
    }

    @Transactional(readOnly = true)
    public List<OrganizerReview> getOrganizerReviews(User user) {
        if (user.getOrganizer() != null) {
            List<OrganizerReview> reviews = user.getOrganizer().getReviews();
            reviews.size(); // Инициализация ленивой загрузки
            return reviews;
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<PlayerReview> getPlayerReviews(User user) {
        List<PlayerReview> reviews = user.getPlayerReviews();
        reviews.size(); // Инициализация
        return reviews;
    }
}
