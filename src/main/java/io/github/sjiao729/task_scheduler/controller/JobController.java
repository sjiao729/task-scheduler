package io.github.sjiao729.taskscheduler.controller;

import java.util.concurrent.atomic.AtomicLong;

import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.entity.Job;
import io.github.sjiao729.taskscheduler.service.JobService;
import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.dtos.JobRequest;
import io.github.sjiao729.taskscheduler.dtos.JobResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController
{
    private final JobService jobService;

    public JobController(JobService jobService)
    {
        this.jobService = jobService;
    }

    /**
     * Creates a new job with the body of the given request
     * @param request the request to create a job with
     * @return returns a response entity with status 201 and the body of the created job
     */
    @PostMapping
    public ResponseEntity<JobResponse> submitJob( @Valid @RequestBody JobRequest request )
    {
        JobResponse created = jobService.submitJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);    
    }

    /**
     * Returns a response with the body of the specified job with ID id
     * @param id the ID of the job to be retrieved
     * @return returns a reponse entity with the OK status and the body of the job to be retrieved
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID id) 
    {
        return ResponseEntity.ok(jobService.getJob(id));
    }
    
    /**
     * Returns a list of all jobs with the specified status, if specified
     * @param status looks for all jobs with this status
     * @return a list of all applicable jobs
     */
    @GetMapping
    public ResponseEntity<List<JobResponse>> listJobs( @RequestParam(required = false) JobStatus status )
    {
        return ResponseEntity.ok(jobService.listJobs(status));
    }

    /**
     * Cancels a job listing
     * @param id the id of the job to be canceled
     * @return 204 No Content with an empty body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelJob( @PathVariable UUID id )
    {
        jobService.cancelJob(id);
        return ResponseEntity.noContent().build();
    }
}