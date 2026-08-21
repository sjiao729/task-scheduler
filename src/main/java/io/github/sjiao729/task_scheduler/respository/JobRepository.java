package io.github.sjiao729.taskscheduler.repository;

import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>
{
    /**
     * Finds jobs based on status
     */
    List<Job> findByStatus( JobStatus status );

    /**
     * Finds pending jobs by FIFO order
     */
    List<Job> findByStatusOrderByCreatedAtAsc(JobStatus status);
}