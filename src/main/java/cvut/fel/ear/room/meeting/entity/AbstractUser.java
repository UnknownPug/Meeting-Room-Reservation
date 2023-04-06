package cvut.fel.ear.room.meeting.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractUser implements Serializable {

    @Enumerated(EnumType.STRING)
    private Role role;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    @Size(max = 20, min = 2, message = "Your username should be greater than 2 and less than 20 ")
    private String username;

    @Column(name = "email", nullable = false)
    @Email
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    @Size(max = 255, min = 8, message = "Your password should be greater than 8 and less than 20")
    private String password;

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public AbstractUser() {
        this.role = Role.ADMIN;
    }
}
