package io.github.sjiao729.taskscheduler.exception;

public class JobNotFoundException extend RuntimeException
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