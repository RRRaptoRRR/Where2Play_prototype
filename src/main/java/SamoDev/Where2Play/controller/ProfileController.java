package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.UserUpdateDto;
import SamoDev.Where2Play.entity.User;
import SamoDev.Where2Play.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("username", user.getName()); // Для хедера
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
}
