package SamoDev.Where2Play.service;

import SamoDev.Where2Play.dao.UserDao;
import SamoDev.Where2Play.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Ищем по логину, если не нашли - пробуем искать по почте (для удобства входа)
        User user = userDao.findByLogin(login)
                .or(() -> userDao.findByGmail(login))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.emptyList() // Ролей пока нет, список пустой
        );
    }
}
