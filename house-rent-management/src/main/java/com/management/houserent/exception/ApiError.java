package com.management.houserent.exception;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp ;

    public ApiError(int status, String error, String message, String path, String timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    //    public ApiError(int status, String error, String message, String path) {
//        this.status = status; this.error = error; this.message = message; this.path = path;
//    }
    // getters

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
