// src/services/taskService.js
const axiosClient = require("../config/axiosClient");
const { v4: uuidv4 } = require("uuid");

let jwtToken = null;

async function loginAndGetToken() {
    const credentials = {
        username: "user",
        role: "ADMIN",
        tenantId: "default"
    };

    const response = await axiosClient.post("/auth/login", credentials);
    jwtToken = response.data.token;
    console.log("✅ Authenticated. Token acquired.");
}

async function createTask(name, orderId, priority = 1) {
    const payload = {
        name,
        payload: { orderId },
        priority,
        delaySeconds: 0,
        maxRetries: 2,
        tenantId: "testTenant",
        notificationUrl: "http://localhost:4000/webhook"
    };

    const headers = {
        Authorization: `Bearer ${jwtToken}`,
        "X-Tenant-ID": "testTenant"
    };

    const response = await axiosClient.post("/tasks", payload, { headers });
    console.log(`🟢 Task "${name}" created: ${response.data.id}`);
    return response.data.name;
}

async function addDependency(taskName, dependsOn) {
    const headers = {
        Authorization: `Bearer ${jwtToken}`,
        "X-Tenant-ID": "testTenant"
    };

    const response = await axiosClient.post(
        "/tasks/dependencies",
        {
            taskName,
            dependsOn
        },
        { headers }
    );

    console.log(`🔗 Dependencies set for ${taskName}: ${dependsOn}`);
}

module.exports = { loginAndGetToken, createTask, addDependency };
