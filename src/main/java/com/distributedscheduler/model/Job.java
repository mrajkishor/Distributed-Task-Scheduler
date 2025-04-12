package main.java.com.distributedscheduler.model;

import java.util.List;
import java.util.Map;

public class Job {

    private String id;
    private String name;
    private List<Task> tasks; // Represents nodes in the DAG
    private RetryPolicy retryPolicy;
    private Schedule schedule;
    private JobStatus status;
    private Map<String, String> metadata;

    // Constructors
    public Job() {}

    public Job(String id, String name, List<Task> tasks, RetryPolicy retryPolicy,
               Schedule schedule, JobStatus status, Map<String, String> metadata) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.retryPolicy = retryPolicy;
        this.schedule = schedule;
        this.status = status;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public RetryPolicy getRetryPolicy() { return retryPolicy; }
    public void setRetryPolicy(RetryPolicy retryPolicy) { this.retryPolicy = retryPolicy; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
