package cvut.fel.ear.room.meeting.dto.request;

public record UserRequest(

        Long id,

        Long reservationId,
        Long paymentId,
        String username,
        String email,
        String password) {
}
