package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.entity.Room;
import cvut.fel.ear.room.meeting.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRooms_whenIsFreeIsTrue() {
        List<Room> freeRooms = new ArrayList<>();
        Room room1 = new Room();
        room1.setId(1L);
        room1.setName("Room 1");
        Room room2 = new Room();
        room2.setId(2L);
        room2.setName("Room 2");
        freeRooms.add(room1);
        freeRooms.add(room2);

        when(roomService.getFreeRooms()).thenReturn(freeRooms);

        ResponseEntity<Iterable<Room>> response = roomController.getRooms(true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<Room>) Objects.requireNonNull(response.getBody())).size());
    }

    @Test
    public void testGetRooms_whenIsFreeIsFalse() {
        List<Room> rooms = new ArrayList<>();
        Room room1 = new Room();
        room1.setId(1L);
        room1.setName("Room 1");
        Room room2 = new Room();
        room2.setId(2L);
        room2.setName("Room 2");
        rooms.add(room1);
        rooms.add(room2);

        when(roomService.getRooms()).thenReturn(rooms);

        ResponseEntity<Iterable<Room>> response = roomController.getRooms(false);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<Room>) Objects.requireNonNull(response.getBody())).size());
    }

    @Test
    public void testGetRoomByName() {
        String roomName = "Room 1";
        Room room = new Room();
        room.setId(1L);
        room.setName(roomName);

        when(roomService.getRoomByName(roomName)).thenReturn(room);

        ResponseEntity<Room> response = roomController.getRoomByName(roomName);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roomName, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void testGetRoomsByCapacityFilter() {
        int num = 2;
        String filterType = "asc";
        List<Room> rooms = Arrays.asList(new Room(), new Room());
        when(roomService.getRoomsByCapacityAsc(num)).thenReturn(new ArrayList<>(rooms));

        ResponseEntity<ArrayList<Room>> response = roomController.getRoomsByCapacityFilter(num, filterType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rooms.size(), Objects.requireNonNull(response.getBody()).size());
        verify(roomService, times(1)).getRoomsByCapacityAsc(num);

    }

    @Test
    public void testGetRoomsByNumFilter() {
        int num = 2;
        String filterType = "asc";
        List<Room> rooms = Arrays.asList(new Room(), new Room());
        when(roomService.getRoomsByNumAsc(num)).thenReturn(new ArrayList<>(rooms));

        ResponseEntity<ArrayList<Room>> response = roomController.getRoomsByNumFilter(num, filterType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rooms.size(), Objects.requireNonNull(response.getBody()).size());
        verify(roomService, times(1)).getRoomsByNumAsc(num);
    }
}