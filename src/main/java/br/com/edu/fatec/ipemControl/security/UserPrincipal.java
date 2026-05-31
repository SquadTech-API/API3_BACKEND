package br.com.edu.fatec.ipemControl.security;

import br.com.edu.fatec.ipemControl.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "admin".equalsIgnoreCase(user.getUserType())
                ? "ROLE_ADMIN"
                : "ROLE_TECHNICIAN";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getPassword()              { return user.getPasswordHash(); }
    @Override public String getUsername()              { return user.getEmail(); }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return user.isActiveEmployee(); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return user.isActiveEmployee(); }
}
