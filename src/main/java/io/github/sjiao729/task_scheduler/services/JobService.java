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

    // get
    public JobResponse getJob( UUID id )
    {
        
    }

    // list

    // cancel
}
