package io.github.sjiao729.taskscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class TaskSchedulerApplication {

	public static void main(String[] args) {
		try
		{
			SpringApplication.run(TaskSchedulerApplication.class, args);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace();
		}
	}

}
