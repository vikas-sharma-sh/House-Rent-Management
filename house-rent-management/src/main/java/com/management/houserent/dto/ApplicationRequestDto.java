package com.management.houserent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ApplicationRequestDto {
    @NotNull
    private Long roomId;

    @Size(max=300)
    @NotBlank
    private String message;

    public  Long getRoomId() {
        return roomId;
    }

    public void setRoomId( Long roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
