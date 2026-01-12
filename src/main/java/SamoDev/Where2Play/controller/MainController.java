package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.entity.User;
import SamoDev.Where2Play.dao.UserDao; // Предположим, он есть, или используем UserDao
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Если нужно подгружать данные пользователя (имя, аватарку)
    // @Autowired private UserService userService;

    @GetMapping("/")
    public String mainMenu(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            // Передаем имя пользователя в шаблон для хедера
            // В реальном проекте лучше достать User из БД по логину
            model.addAttribute("username", userDetails.getUsername());
        }
        return "main-menu";
    }
}
