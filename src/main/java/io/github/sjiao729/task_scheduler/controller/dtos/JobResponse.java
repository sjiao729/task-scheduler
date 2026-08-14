package io.github.sjiao729.taskscheduler;

import io.github.sjiao729.taskscheduler.entity.Job;
import io.github.sjiao729.taskscheduler.entity.JobStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

/**
 * DTO for read-only view of a Job to clients
 */
public class JobResponse
{
    @Getter private UUID id;
    @Getter private String payload;
    @Getter private JobStatus status;
    @Getter private int retryCount;
    @Getter private Instant createdAt;
    @Getter private Instant updatedAt;

    /**
     * Constructs a JobResponse from the given job
     * @param job the job to construct JobResponse from
     * @return returns the created dto
     */
    public static JobResponse fromEntity(Job job)
    {
        JobResponse dto = new JobResponse();
        dto.id = job.getId();
        dto.payload = job.getPayload();
        dto.status = job.getStatus();
        dto.retryCount = job.getRetryCount();
        dto.createdAt = job.getCreatedAt();
        dto.updatedAt = job.getUpdatedAt();
        return dto;
    }
}