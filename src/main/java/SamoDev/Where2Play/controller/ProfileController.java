package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.ReviewDto;
import SamoDev.Where2Play.dto.UserUpdateDto;
import SamoDev.Where2Play.entity.User;
import SamoDev.Where2Play.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public String getProfilePage(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        User user = userProfileService.getCurrentUser(currentUsername);

        model.addAttribute("user", user);
        model.addAttribute("username", user.getNickname()); // Для хедера
        model.addAttribute("organizerReviews", userProfileService.getOrganizerReviews(user));
        model.addAttribute("playerReviews", userProfileService.getPlayerReviews(user));
        model.addAttribute("isOrganizer", user.getOrganizer() != null);

        return "profile";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserUpdateDto userUpdateDto, Authentication authentication) {
        userProfileService.updateUserProfile(authentication.getName(), userUpdateDto);
        return "redirect:/profile"; // Перезагружаем страницу после сохранения
    }

    @GetMapping("/{id}")
    public String getPublicProfile(@PathVariable("id") Integer userId, Model model, Authentication authentication) {
        // Проверяем, не смотрит ли пользователь свой же профиль
        String currentUsername = authentication.getName();
        User currentUser = userProfileService.getCurrentUser(currentUsername);

        if (currentUser.getId().equals(userId)) {
            return "redirect:/profile"; // Перенаправляем на личный профиль
        }

        User user = userProfileService.getUserById(userId);

        model.addAttribute("user", user);
        model.addAttribute("username", currentUser.getNickname()); // Для хедера (имя того, кто смотрит)
        model.addAttribute("organizerReviews", userProfileService.getOrganizerReviews(user));
        model.addAttribute("playerReviews", userProfileService.getPlayerReviews(user));
        model.addAttribute("isOrganizer", user.getOrganizer() != null);

        return "public-profile";
    }

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable("id") Integer userId,
                            @ModelAttribute ReviewDto reviewDto,
                            Authentication authentication) {
        userProfileService.addReview(userId, authentication.getName(), reviewDto);
        return "redirect:/profile/" + userId;
    }

}
