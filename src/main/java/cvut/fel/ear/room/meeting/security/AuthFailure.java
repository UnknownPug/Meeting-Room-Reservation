package cvut.fel.ear.room.meeting.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cvut.fel.ear.room.meeting.security.model.LoginStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
@Service
public class AuthFailure implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthFailure.class);

    private final ObjectMapper mapper;

    @Autowired
    public AuthFailure(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException {
        LOG.debug("Login failed for user {}.", httpServletRequest.getParameter(SecurityConstants.USERNAME_PARAM));
        final LoginStatus status = new LoginStatus(false, false, null, e.getMessage());
        mapper.writeValue(httpServletResponse.getOutputStream(), status);
    }
}
