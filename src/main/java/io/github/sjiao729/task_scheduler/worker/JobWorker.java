package io.github.sjiao729.taskscheduler.worker;

import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.entity.Job;
import io.github.sjiao729.taskscheduler.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.time.Instant;

@Component
public class JobWorker
{
    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);
    private final JobRepository jobRepository;
    private final Random random = new Random();
    private final long BASE_DELAY = 10;

    public JobWorker(JobRepository jobRepository) 
    {
        this.jobRepository = jobRepository;
    }

    /**
     * Finds and list all eligible jobs in jobRepository by order created, then
     * processes them
     */
    @Scheduled( fixedDelay = 5000 )
    public void pollAndProcessJobs()
    {
        List<Job> eligibleJobs = jobRepository.findEligibleJobs( JobStatus.PENDING, Instant.now() );

        if( pendingJobs.isEmpty() )
        {
            log.debug( "No pending jobs found." );
            return;
        }

        log.info( "Found {} pending job(s) to process.", pendingJobs.size() );

        for( Job job : eligibleJobs )
        {
            processJob( job );
        }
    }

    /**
     * Simulates processing a job with an 80% success rate
     * @param job the job to be processed
     */
    private void processJob( Job job )
    {
        job.setStatus( JobStatus.RUNNING );
        jobRepository.save( job );
        log.info( "Job with ID {} started.", job.getId() );

        try
        {
            Thread.sleep( 2000 ); // Simulates the job being done
            boolean success = random.nextDouble() < 0.8; // 80% success rate

            if( success )
            {
                job.setStatus( JobStatus.DONE );
                log.info( "Job with ID {} was successfully completed.", job.getId() );
            }
            else
            {
                handleFailure( job );
            }
        }
        catch( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            job.setStatus( JobStatus.FAILED );
            log.error( "Job with ID {} was interrupted.", job.getId() );
        }

        jobRepository.save( job );
    }

    /**
     * Adds exponential delay and requeues job for retry if the maximnum retry
     * attempts haven't been exceeded
     * @param job the job which was failed
     */
    private void handleFailure( Job job )
    {
        int currAttempts = job.getRetryCount() + 1;
        if( currAttempts >= job.getMaxRetries() )
        {
            job.setStatus( JobStatus.FAILED );
            log.warn( "Job with ID {} failed permanently after {} attempts.", 
                job.getId(), currAttempts );
            return;
        }

        long delay = BASE_DELAY * (long)(Math.pow( 2, job.getRetryCount() ));
        job.setRetryCount( currAttempts );
        job.setStatus( JobStatus.PENDING );
        job.setNextAttemptAt( (Instant.now()).plusSeconds(delay) );
    }
}