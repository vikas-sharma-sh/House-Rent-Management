package com.management.houserent.repository;

import com.management.houserent.model.Owner;
import com.management.houserent.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByOwner_Id(Long ownerId);
    List<Room> findByAvailabilityStatus(Room.AvailabilityStatus status);
    Optional<Room> findByOwner_IdAndAddressAndRoomNumber(Long ownerId, String address, String roomNumber);

    Optional<Room> findByIdAndOwner(Long roomId, Owner owner);

    default List<Room> findAvailableRooms() {
        return findByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);

    }


    Long countByAvailabilityStatus(Room.AvailabilityStatus status);
    // Count available rooms
    default Long countAvailableRooms() {
        return countByAvailabilityStatus(Room.AvailabilityStatus.AVAILABLE);
    }


}
