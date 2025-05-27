const express = require('express');
const app = express();
const PORT = 4000;

app.use(express.json());

let logs = [];

app.post('/webhook', (req, res) => {
    const logEntry = {
        timestamp: new Date().toISOString(),
        payload: req.body,
    };
    logs.push(logEntry);
    if (logs.length > 50) logs.shift(); // keep last 50
    console.log('📬 Webhook received:', JSON.stringify(logEntry, null, 2));
    res.status(200).send('✅ Webhook received');
});

app.get('/logs', (req, res) => {
    res.json(logs);
});

app.listen(PORT, () => {
    console.log(`🚀 Webhook mock server running on http://localhost:${PORT}`);
});
