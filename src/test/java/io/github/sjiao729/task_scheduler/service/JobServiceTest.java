package io.github.sjiao729.taskscheduler.service;

import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.entity.Job;
import io.github.sjiao729.taskscheduler.dtos.JobRequest;
import io.github.sjiao729.taskscheduler.dtos.JobResponse;
import io.github.sjiao729.taskscheduler.repository.JobRepository;
import io.github.sjiao729.taskscheduler.exception.JobNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest
{
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private Job existingJob;
    private Job existingJob2;
    private List<Job> jobList;

    @BeforeEach
    void setUp()
    {
        existingJob = new Job();
        existingJob.setId( UUID.randomUUID() );
        existingJob.setPayload( "send email" );
        existingJob.setStatus( JobStatus.PENDING );

        existingJob2 = new Job();
        existingJob2.setId( UUID.randomUUID() );
        existingJob2.setPayload( "update database" );
        existingJob2.setStatus( JobStatus.RUNNING );

        jobList = new ArrayList<>();
    }

    /**
     * Tests that submitting a job with a service saves it with the correct
     * payload and the pending status in the repo
     */
    @Test
    void testSubmitJob_savesJobWithPendingStatus()
    {
        JobRequest request = new JobRequest();
        request.setPayload( "send email " );

        when( jobRepository.save( any(Job.class) ) ).thenReturn( existingJob );

        JobResponse response = jobService.submitJob( request );

        assertThat(response.getPayload()).isEqualTo("send email");
        assertThat(response.getStatus()).isEqualTo(JobStatus.PENDING);
        verify(jobRepository).save(any(Job.class));
    }

    /**
     * Tests that getJob can find existing jobs by ID
     */
    @Test
    void testGetJob_returnsJob_whenFound()
    {
        when( jobRepository.findById( existingJob.getId() ) )
            .thenReturn( Optional.of(existingJob) );

        JobResponse response = jobService.getJob( existingJob.getId() );

        assertThat( response.getId() ).isEqualTo( existingJob.getId() );
    }

    /**
     * Tests that when a job is missing, throws a JobNotFoundException.
     */
    @Test
    void testGetJob_throwsExceptionWhenMissing()
    {
        UUID fakeId = UUID.randomUUID();

        when( jobRepository.findById(fakeId) ).thenReturn( Optional.empty() );

        assertThatThrownBy( () -> jobService.getJob(fakeId) )
            .isInstanceOf(JobNotFoundException.class)
            .hasMessageContaining( fakeId.toString() );

    }

    /**
     * Tests that calling listJobs with no specified status return all jobs.
     */
    @Test
    void testListJobs_noStatus()
    {
        jobList.add( existingJob );
        jobList.add( existingJob2 );

        when( jobRepository.findAll() ).thenReturn( jobList );

        List<JobResponse> response = jobService.listJobs( null );

        assertThat( response.size() ).isEqualTo( 2 );
    }

    /**
     * Tests that calling listJobs with specified status return matching jobs.
     */
    @Test
    void testListJobs_withStatus()
    {
        jobList.add( existingJob2 );

        when( jobRepository.findByStatus( JobStatus.RUNNING ) ).thenReturn( jobList );

        List<JobResponse> response = jobService.listJobs( JobStatus.RUNNING );

        assertThat( response.size() ).isEqualTo( 1 );
        assertThat( response.get( 0 ).getId() ).isEqualTo( existingJob2.getId() );
    }
}