package io.github.sjiao729.taskscheduler.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO to isolate client payload
 */
public class JobRequest {

    @NotBlank(message = "payload must not be blank")
    private String payload;

    public String getPayload() { return payload; }

    public void setPayload(String payload) { this.payload = payload; }
}