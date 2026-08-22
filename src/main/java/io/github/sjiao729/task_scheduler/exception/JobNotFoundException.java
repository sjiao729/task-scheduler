package io.github.sjiao729.taskscheduler.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException
{
    public JobNotFoundException()
    {
        super();
    }

    public JobNotFoundException( UUID id )
    {
        super( "Job with ID " + id + " cannot be found." );
    }
}