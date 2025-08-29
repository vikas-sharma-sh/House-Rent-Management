package com.management.houserent.repository;

import com.management.houserent.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // find all rooms for an owner (owner.id)
    List<Room> findByOwner_Id(Long ownerId);

    // find rooms by availability
    List<Room> findByAvailabilityStatus(Room.AvailabilityStatus status);

    // check uniqueness: owner + address + roomNumber
    Optional<Room> findByOwner_IdAndAddressAndRoomNumber(Long ownerId, String address, String roomNumber);
}
