package cvut.fel.ear.room.meeting.util;

import cvut.fel.ear.room.meeting.entity.Role;

public final class Constants {

    /*
     * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.USER;

    private Constants() {
        throw new AssertionError();
    }
}
