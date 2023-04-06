package cvut.fel.ear.room.meeting.dto.request;

public record AdminRequest(
        Long id,
        Long roomId,
        Long reservationId,
        String username,
        String email,
        String password
) {
}
