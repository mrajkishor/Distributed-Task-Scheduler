package com.distributedscheduler.repository;

import com.distributedscheduler.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRedisRepository extends CrudRepository<Task, String> {
}
