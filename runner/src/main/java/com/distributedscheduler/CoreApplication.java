package com.distributedscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}

/**
 * About thsi component \
 *
 *This is the **main entry point** for your Spring Boot application. Let’s break it down:
 *
 * ---
 *
 * ### 🔹 `@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })`
 *
 * * Tells Spring Boot to **auto-configure** the app, but **excludes JDBC datasource config** (i.e., no SQL database is used).
 * * Your app likely uses **Redis** or other storage instead of traditional databases.
 *
 * ---
 *
 * ### 🔹 `@EnableScheduling`
 *
 * * Enables Spring’s **scheduled task execution** (e.g., running background jobs periodically using `@Scheduled` annotations).
 *
 * ---
 *
 * ### 🔹 `public static void main(String[] args)`
 *
 * * Standard Java main method.
 * * Calls `SpringApplication.run(...)` to **start the application**, including the Spring context, beans, and auto-configurations.
 *
 * ---
 *
 * ### ✅ Summary:
 *
 * This class boots your **Redis-based Distributed Task Scheduler**, enables **background jobs**, and disables **SQL datasource config** since your project doesn’t use a traditional DB.
 *
 * **/