package cvut.fel.ear.room.meeting.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
public class AuthToken extends AbstractAuthenticationToken implements Principal {

    private final UserDetails userDetails;

    public AuthToken(Collection<? extends GrantedAuthority> authorities, UserDetails userDetails) {
        super(authorities);
        this.userDetails = userDetails;
        super.setAuthenticated(true);
        super.setDetails(userDetails);
    }

    @Override
    public String getCredentials() {
        return userDetails.getPassword();
    }

    @Override
    public UserDetails getPrincipal() {
        return userDetails;
    }
}
