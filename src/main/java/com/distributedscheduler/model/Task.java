package main.java.com.distributedscheduler.model;


public class Task {
    private String id;
    private String name;
    private List<String> dependencies; // List of task IDs
    private TaskStatus status;
    private String log;

    // Constructors, Getters, Setters
}
