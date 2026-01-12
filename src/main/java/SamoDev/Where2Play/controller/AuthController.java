package SamoDev.Where2Play.controller;

import SamoDev.Where2Play.dto.RegistrationDto;
import SamoDev.Where2Play.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") RegistrationDto registrationDto, Model model) {
        try {
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                throw new RuntimeException("Пароли не совпадают");
            }
            authService.registerUser(registrationDto);
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
