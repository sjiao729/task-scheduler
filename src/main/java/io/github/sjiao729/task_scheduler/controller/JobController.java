package io.github.sjiao729.taskscheduler;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController
{
    @GetMapping("/status")
    public String status(@RequestParam String id)
    {

    }
}