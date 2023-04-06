package cvut.fel.ear.room.meeting.security;

import cvut.fel.ear.room.meeting.security.model.AuthToken;
import cvut.fel.ear.room.meeting.security.model.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
@Service
public class DefaultAuthProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthProvider.class);

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultAuthProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(authentication.getName());
        if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            userDetails.eraseCredentials();
            return SecurityUtil.setCurrentUser(userDetails);
        } else {
            throw new BadCredentialsException(LOG.getName() + " password is invalid.");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass) ||
                AuthToken.class.isAssignableFrom(aClass);
    }
}
