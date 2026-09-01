package io.github.sjiao729.taskscheduler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
/**
 * Entity that represents a new job available to be picked up
 */
public class Job
{
    // Job ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;   

    // Payload
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    // Job status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.PENDING;

    // Number of times retried
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    // Maximum number of retires allowed
    @Column(name = "max_retries", nullable = false)
    private int maxRetries = 3;

    // Delay before next retry
    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    // Time created at
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Last updated at
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate()
    {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = Instant.now();
    }

}