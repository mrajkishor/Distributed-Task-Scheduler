
# 📬 Webhook Mock Server

This is a simple **Express.js-based mock webhook server** for testing webhook integrations, especially useful for **DAG schedulers**, **job queues**, and **task orchestration systems**.

---

## 🚀 Features

- Receives webhook events via `POST /webhook`
- Logs each incoming payload with a timestamp
- Stores the latest 50 webhook events in memory
- View logs at `GET /logs`
- Console logs every incoming webhook request

---

## 📦 Setup

### 1. Clone the Repo
```bash
git clone https://github.com/your-org/webhook-mock-server.git
cd webhook-mock-server
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Run the Server

```bash
node server.js
```

The server will start at:
`http://localhost:4000`

---

## 📡 API Endpoints

### 🔹 `POST /webhook`

Receives and logs incoming webhook data.

**Example Request:**

```bash
curl -X POST http://localhost:4000/webhook \
  -H "Content-Type: application/json" \
  -d '{ "taskName": "CheckInventory", "status": "COMPLETED", "orderId": "ORD123" }'
```

**Response:**

```json
✅ Webhook received
```

---

### 🔹 `GET /logs`

Returns the most recent 50 webhook payloads received.

**Example:**

```bash
curl http://localhost:4000/logs
```

**Response:**

```json
[
  {
    "timestamp": "2025-09-27T07:31:45.231Z",
    "payload": {
      "taskName": "CheckInventory",
      "status": "COMPLETED",
      "orderId": "ORD123"
    }
  }
]
```

---

## 🔧 Use Cases

* Simulate webhook callbacks for async tasks
* Debug webhook integration in local development
* Visualize task execution lifecycle (e.g., in DAG orchestration)

---

## 📄 License

MIT

---

## 🙋‍♂️ Author

Created by [Rajkishor Maharana](https://github.com/mrajkishor)

