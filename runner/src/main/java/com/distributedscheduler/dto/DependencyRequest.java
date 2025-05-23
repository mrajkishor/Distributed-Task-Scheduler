
package com.distributedscheduler.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class DependencyRequest {

    @NotNull
    private String taskName;

    @NotEmpty
    private List<String> dependsOn;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }
}
