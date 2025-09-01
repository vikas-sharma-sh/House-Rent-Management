package com.management.houserent.config;

import com.management.houserent.model.Role;
import com.management.houserent.model.User;
import com.management.houserent.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seeAdmin(UserRepository userRepo, PasswordEncoder encoder){
        return args -> {
            String adminEmail = "admin@houserent.local";
            if(!userRepo.existsByEmail(adminEmail)){
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setRole("ROLE_ADMIN");
                userRepo.save(admin);
                System.out.println("Seeded default Admin: "+ adminEmail +"/Admin@123");
            }
        };
    }
}
