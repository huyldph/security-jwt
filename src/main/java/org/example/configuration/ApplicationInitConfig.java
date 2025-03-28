package org.example.configuration;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.enums.RoleEnum;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(RoleEnum.ADMIN.name());

                User user = User.builder()
                        .firstName("demo")
                        .lastName("admin")
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("admin@gmail.com")
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has bean created with default password: admin, please change it");
            }
        };
    }
}
