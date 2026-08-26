package io.github.sjiao729.taskscheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sjiao729.taskscheduler.repository.JobRepository;
import io.github.sjiao729.taskscheduler.entity.JobStatus;
import io.github.sjiao729.taskscheduler.dtos.JobRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class JobControllerIntegrationTest
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobRepository jobRepository;

    /**
     * Wipes existing database entries before each test
     */
    @BeforeEach
    void cleanDb()
    {
        jobRepository.deleteAll();
    }

    /**
     * Tests that submitting a job returns a 201 created and successfully 
     * creates the job in the repository.
     */
    @Test
    void testSubmitJob_return201_persists() throws Exception
    {
        JobRequest request = new JobRequest();
        request.setPayload("send email");

        mockMvc.perform( post("/api/jobs")
                    .contentType("application/json")
                .   content( objectMapper.writeValueAsString(request) ) )
                .andExpect( status().isCreated() )
                .andExpect( jsonPath("$.payload").value("send email") )
                .andExpect( jsonPath("$.status").value("PENDING") )
                .andExpect( jsonPath("$.id").exists() );

        assertThat( jobRepository.count() ).isEqualTo(1);
    }

    /**
     * Tests that submitting a job with no payloads return a 400 bad request and
     * does not create a job
     */
    @Test
    void testSubmitJob_return400_whenPayloadBlank() throws Exception
    {
        JobRequest request = new JobRequest();
        request.setPayload( "" );

        mockMvc.perform( post("/api/jobs")
                .contentType("application/json")
                .content( objectMapper.writeValueAsString(request) ) )
            .andExpect( status().isBadRequest() )
            .andExpect( jsonPath("$.fieldErrors.payload").exists() );

        assertThat( jobRepository.count() ).isEqualTo(0);
    }

    /**
     * Tests that getJob returns 404 not found when searching for non-existent
     * jobs.
     */
    @Test
    void testGetJob_return404_whenDoesNotExist() throws Exception
    {
        mockMvc.perform( get("/api/jobs/00000000-0000-0000-0000-000000000000") )
            .andExpect( status().isNotFound() )
            .andExpect( jsonPath("$.message").value( containsString( "cannot be found" ) ) );
    }

    @Test
    void testListJobs() throws Exception
    {
        JobRequest request = new JobRequest();
        request.setPayload( "send email" );
        JobRequest request2 = new JobRequest();
        request2.setPayload( "update database" );

        mockMvc.perform( post("/api/jobs") 
            .contentType("application/json")
            .content( objectMapper.writeValueAsString( request ) ) );
        mockMvc.perform( post("/api/jobs") 
            .contentType("application/json")
            .content( objectMapper.writeValueAsString( request2 ) ) );

        mockMvc.perform( get("/api/jobs") )
            .andExpect( status().isOk() )
            .andExpect( jsonPath("$", hasSize( 2 )) )
            .andExpect( jsonPath("$[0].payload").value("send email") )
            .andExpect( jsonPath("$[1].payload").value("update database") ); 
    }
}