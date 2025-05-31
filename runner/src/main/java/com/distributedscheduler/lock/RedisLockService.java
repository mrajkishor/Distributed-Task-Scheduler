package com.distributedscheduler.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Tries to acquire a lock for a task using SETNX.
     * @param taskId The unique task ID
     * @param ttlMillis Time-to-live in milliseconds
     * @return true if lock was acquired, false otherwise
     */
    public boolean acquireLock(String taskId, long ttlMillis) {
        String key = "lock:task:" + taskId;
        String lockValue = UUID.randomUUID().toString(); // For future ownership validation

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, lockValue, Duration.ofMillis(ttlMillis));

        // Duration.ofMillis(ttlMillis):
        // It specifies how long the lock should live in Redis before it auto-expires.

        // 🔹 Why Use It?
        // - Prevents deadlocks if a process crashes and forgets to release the lock.
        //- Ensures the lock auto-expires after ttlMillis (e.g., 5000 ms = 5 seconds).


        return Boolean.TRUE.equals(success);
    }

    /**
     * Releases the lock for the given task.
     * You can extend this to only release if `lockValue` matches (ownership).
     */
    public void releaseLock(String taskId) {
        String key = "lock:task:" + taskId;
        redisTemplate.delete(key);
    }
}


// Q. What if the task takes more time running than 5 sec (Which is TTL)?

/***
 * If the task runs longer than the lock's TTL (e.g., 5 seconds), the Redis key will **automatically expire**, and:
 *
 * ### 🔴 Problem:
 *
 * Another worker **might acquire the same lock** and start executing the **same task again**, leading to:
 *
 * * **Duplicate execution**
 * * **Race conditions**
 * * **Inconsistent data**
 *
 * ---
 *
 * ### ✅ Solutions:
 *
 * #### 1. **Estimate TTL Carefully**
 *
 * Set `ttlMillis` based on expected task duration + buffer.
 *
 * #### 2. **Renew the Lock (Heartbeat)**
 *
 * Use a **background thread** to periodically extend the lock TTL while the task is running.
 *
 * ```java
 * // every 2 seconds
 * redisTemplate.expire("lock:task:" + taskId, Duration.ofMillis(newTTL));
 * ```
 *
 * #### 3. **Lock Ownership Validation (Advanced)**
 *
 * Store a unique `lockValue` (UUID) when acquiring the lock and check it before releasing/renewing to ensure ownership.
 *
 * ---
 *
 * ### 🧠 Recommended for production:
 *
 * Use **Redisson's `RLock` with `lock()` + `unlock()`** or **implement a lock watchdog** if you build your own.
 *
 *
 *
 * ***/
