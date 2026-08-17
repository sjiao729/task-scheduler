package io.github.sjiao729.taskscheduler.service;

import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.entity.Job;
import io.github.sjiao729.taskscheduler.controller.dtos.JobRequest;
import io.github.sjiao729.taskscheduler.controller.dtos.JobResponse;
import io.github.sjiao729.taskscheduler.repository.JobRepository;
import io.github.sjiao729.taskscheduler.exception.JobNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class JobService
{
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) 
    {
        this.jobRepository = jobRepository;
    }

    /**
     * Creates a new job and saves it to the repository
     * @param request the request for the new job
     * @return a JobResponse for the new job created
     */
    public JobResponse submitJob( JobRequest request )
    {
        Job created = new Job();
        created.setPayload( request.getPayload() );
        created.setStatus(JobStatus.PENDING);
        Job saved = jobRepository.save(created);
        return JobResponse.fromEntity( saved );   
    }

    /**
     * Attempts to find and return a job with the given id
     * @param id the id of the job to find
     * @return a JobResponse for the job, if found
     */
    public JobResponse getJob( UUID id )
    {
        Job target = jobRepository.findByIt( id )
                .orElseThrow( () -> new JobNotFoundException(id) );
        return JobResponse.fromEntity( target );
    }

    /**
     * Lists all jobs with the given status, if specified
     * @param status the method looks for jobs with this status, or all jobs if 
     * not specified
     * @return a list of JobResponses that was found by this method
     */
    public List<JobResponse> listJobs( JobStatus status )
    {
        List<Job> list = ( status == null ) ? 
            jobRepository.findAll() : jobRepository.findByStatus( status );
        List<JobResponse> results = list.stream()
                .map( JobResponse::fromEntity )
                .collect(Collectors.toList());
        return results;
    }

    /**
     * Cancels the job with the given id
     * @param id the id of the job to be cancelled
     */
    public void cancelJob( UUID id )
    {
        Job job = jobRepository.findById( id )
                .orElseThrow( () -> JobNotFoundException(id) );
        job.setStatus(JobStatus.FAILED);
        jobRepository.save( job );
    }
}
