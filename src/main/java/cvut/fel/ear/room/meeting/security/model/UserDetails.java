package cvut.fel.ear.room.meeting.security.model;

import cvut.fel.ear.room.meeting.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private final User user;

    private final Set<GrantedAuthority> authorities;

    public UserDetails(User user) {
        Objects.requireNonNull(user);
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
    }

    private void addUserRole() {
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
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

    public User getUser() {
        return user;
    }

    public void eraseCredentials() {
        user.erasePassword();
    }
}
