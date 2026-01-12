package SamoDev.Where2Play.dto;

import lombok.Data;

@Data
public class RegistrationDto {
    private String name;        // ФИО
    private String phone;       // Телефон
    private String login;       // Логин
    private String email;       // Почта (gmail)
    private String password;    // Пароль
    private String confirmPassword; // Повтор пароля
}
