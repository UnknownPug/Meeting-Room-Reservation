package cvut.fel.ear.room.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ReservationRequest(
        Long id,
        Long roomId,
        Long adminId,
        Double price,
        @JsonProperty("reservation_date_time_start")
        LocalDateTime reservationDateTimeStart,
        @JsonProperty("reservation_date_time_end")
        LocalDateTime reservationDateTimeEnd
) {
}
