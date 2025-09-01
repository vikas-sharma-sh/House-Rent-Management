package com.management.houserent.dto;



public class AdminStatsDto {

    private long totalOwner;
    private long totalTenants;
    private long totalRooms;
    private long occupiedRooms;
    private long availableRooms;

    public long getTotalOwner() {
        return totalOwner;
    }

    public void setTotalOwner(long totalOwner) {
        this.totalOwner = totalOwner;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }
}
