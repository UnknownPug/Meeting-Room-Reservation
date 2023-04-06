package cvut.fel.ear.room.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // Spring without security
@SpringBootApplication //  Spring with security
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
