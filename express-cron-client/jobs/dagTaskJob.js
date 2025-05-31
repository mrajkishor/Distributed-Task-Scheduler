// src/jobs/dagTaskJob.js
const cron = require("node-cron");
const { loginAndGetToken, createTask, addDependency } = require("../services/taskService");

async function runDAGJob() {
    try {
        await loginAndGetToken();

        const orderId = "ORD-" + Math.floor(Math.random() * 100000);

        const t1 = await createTask("CheckInventory", orderId);
        const t2 = await createTask("ConfirmPayment", orderId);
        const t3 = await createTask("PackageOrder", orderId);
        const t4 = await createTask("ShipOrder", orderId);

        await addDependency("ConfirmPayment", [t1]);
        await addDependency("PackageOrder", [t2]);
        await addDependency("ShipOrder", [t3]);

    } catch (err) {
        console.error("❌ Error in DAG job:", err.message);
    }
}

function scheduleDagTask() {
    cron.schedule("*/5 * * * *", () => {
        console.log("🕒 Running DAG task job:", new Date().toISOString());
        runDAGJob();
    });
}

module.exports = { scheduleDagTask, runDAGJob };
