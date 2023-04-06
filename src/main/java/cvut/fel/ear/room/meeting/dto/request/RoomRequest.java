package cvut.fel.ear.room.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record RoomRequest(
        Long id,
        Long reservationId,
        String name,
        Double pricePerHour,
        String description,
        @JsonProperty("reservation_date_time_start")
        LocalDateTime reservationDateTimeStart,
        @JsonProperty("reservation_date_time_end")
        LocalDateTime reservationDateTimeEnd,
        Integer roomCapacity
) {
}
