package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.OrganizerReviewDao;
import SamoDev.Where2Play.dao.PlayerReviewDao;
import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.dto.ReviewDto;
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
    private final PlayerReviewDao playerReviewDao;
    private final OrganizerReviewDao organizerReviewDao;

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

    // ... существующие методы ...

    @Transactional(readOnly = true)
    public User getUserById(Integer userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional
    public void addReview(Integer targetUserId, String authorUsername, ReviewDto reviewDto) {
        User targetUser = getUserById(targetUserId);
        User author = getCurrentUser(authorUsername);

        if (targetUser.getId().equals(author.getId())) {
            throw new RuntimeException("Нельзя оставлять отзывы самому себе");
        }

        if ("PLAYER".equals(reviewDto.getType())) {
            PlayerReview review = new PlayerReview();
            review.setUser(targetUser);
            review.setReview(reviewDto.getText());
            // Если в сущности PlayerReview есть поле автора, добавьте: review.setAuthor(author);
            // Если нет, то отзыв анонимный или нужно доработать БД.
            // Пока сохраняем как есть в вашей структуре.
            // (Вам нужно добавить PlayerReviewDao и сохранить через него, или через каскад)
            // Предположим, у вас есть playerReviewDao
            playerReviewDao.save(review);

        } else if ("ORGANIZER".equals(reviewDto.getType())) {
            if (targetUser.getOrganizer() == null) {
                throw new RuntimeException("Этот пользователь не является организатором");
            }
            OrganizerReview review = new OrganizerReview();
            review.setOrganizer(targetUser.getOrganizer());
            review.setReview(reviewDto.getText());
            organizerReviewDao.save(review);
        }
    }

}
