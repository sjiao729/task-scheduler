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

    /**
     * Finds jobs with the specified status which are scheduled for retry 
     * already, in FIFO order
     * @param status the status of the jobs to search for
     * @param now the current time, to determine if a job is already scheduled
     */
    @Query( "SELECT j FROM Job j " + "WHERE j.status = :status " + 
        "AND (j.nextAttemptAt IS NULL OR j.nextAttemptAt <= :now) " + 
        "ORDER BY j.createdAt ASC" )
    List<Job> findEligibleJobs( @Param("status") JobStatus status, 
        @Param("now") Instant now );
}