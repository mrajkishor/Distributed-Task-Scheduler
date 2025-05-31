// index.js

const express = require("express");
const { scheduleDagTask, runDAGJob } = require("./jobs/dagTaskJob");

const app = express();
app.use(express.json());

app.get("/", (req, res) => {
    res.send("📡 DAG Task Client Running");
});

// ✅ Manual trigger endpoint
app.post("/generate-now", async (req, res) => {
    await runDAGJob();
    res.status(200).send("DAG tasks created successfully.");
});

// ✅ Start scheduled cron
scheduleDagTask();

const PORT = 4001;
app.listen(PORT, () => {
    console.log(`🚀 Server running at http://localhost:${PORT}`);
});
