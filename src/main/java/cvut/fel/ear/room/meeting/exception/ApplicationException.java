package cvut.fel.ear.room.meeting.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    private String message;
    private HttpStatus status;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String msg, HttpStatus status) {
        this.message = msg;
        this.status = status;
    }
}
