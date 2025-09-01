package com.management.houserent.security;


import com.management.houserent.model.User;
import com.management.houserent.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepo ;


    public CustomUserDetailsService(UserRepository userRepo){
        this.userRepo = userRepo ;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User u = userRepo.findByEmail(email.toLowerCase().trim())
                .orElseThrow(()->new UsernameNotFoundException("User not found: " + email));

//        return  new org.springframework.security.core.userdetails.User(
//                u.getEmail(),
//                u.getPassword(),
//                List.of(new SimpleGrantedAuthority(u.getRole().name()))
//        );
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole().name().replace("ROLE_", "")) // Spring adds ROLE_ prefix
                .build();
    }

}
