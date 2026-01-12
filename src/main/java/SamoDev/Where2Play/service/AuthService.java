package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.dto.RegistrationDto;
import SamoDev.Where2Play.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegistrationDto dto) {
        if (userDao.findByLogin(dto.getLogin()).isPresent()) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        if (userDao.findByGmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с такой почтой уже существует");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setLogin(dto.getLogin());
        user.setGmail(dto.getEmail());
        user.setNickname(dto.getLogin()); // По умолчанию никнейм = логин
        // Хешируем пароль перед сохранением!
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userDao.save(user);
    }
}
