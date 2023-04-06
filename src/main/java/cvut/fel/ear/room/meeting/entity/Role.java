package cvut.fel.ear.room.meeting.entity;

/*
 * Source: https://gitlab.fel.cvut.cz/ear/b221-eshop
 */
public enum Role {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
