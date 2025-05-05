package com.pizzeria.userservice.utils.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pizzeria.userservice.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(user.getUserRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" +user.getUserRole().name()));
        }

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
