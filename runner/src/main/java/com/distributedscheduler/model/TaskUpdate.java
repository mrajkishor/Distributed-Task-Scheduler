package com.distributedscheduler.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the updatable fields of a Task.
 * Used in PUT /jobs/{id}/tasks/{taskId} API for updating task info.
 */
public class TaskUpdate {

    private String name;
    private List<String> dependencies;
    private Map<String, String> metadata;

    public TaskUpdate() {
    }

    public TaskUpdate(String name, List<String> dependencies, Map<String, String> metadata) {
        this.name = name;
        this.dependencies = dependencies;
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}



/**
 *
 * The `TaskUpdate` class represents the **updatable fields of a Task** when calling the API endpoint `PUT /jobs/{id}/tasks/{taskId}`.
 *
 * ### 🔍 Fields Explained:
 *
 * * `name`: Updates the name (label) of the task.
 * * `dependencies`: Updates the list of other task IDs this task depends on (for DAG).
 * * `metadata`: Allows attaching extra key-value info like tags, labels, annotations, etc.
 *
 * This is useful for partial updates to an existing task without recreating it.
 *
 *
 * **/
