package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Role;
import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */

@Component
public class SystemInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    private static final String ADMIN_USERNAME = "adminTest";
    private static final String ADMIN_EMAIL = "admin@application.cz";
    private static final String ADMIN_PASSWORD = "test1234";

    private final UserService service;

    private final PlatformTransactionManager txManager;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public SystemInitializer(UserService service,
                             PlatformTransactionManager txManager,
                             PasswordEncoder passwordEncoder,
                             UserRepository userRepository) {
        this.service = service;
        this.txManager = txManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generateAdmin();
            return null;
        });
    }

    /**
     * Generates an admin account if it does not already exist.
     */
    private void generateAdmin() {
        if (service.existsUsername(ADMIN_USERNAME) || service.existsEmail(ADMIN_EMAIL)) {
            return;
        }
        final User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(ADMIN_PASSWORD);
        admin.encodePassword(passwordEncoder);
        admin.setRole(Role.ADMIN);
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        userRepository.save(admin);
    }
}
