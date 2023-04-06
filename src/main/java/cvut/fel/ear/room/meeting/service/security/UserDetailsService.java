package cvut.fel.ear.room.meeting.service.security;

import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import cvut.fel.ear.room.meeting.entity.User;


/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public UserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new cvut.fel.ear.room.meeting.security.model.UserDetails(user);
    }
}

