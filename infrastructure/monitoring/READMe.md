## 📊 Grafana Monitoring Setup

Prometheus metrics are visualized via Grafana. Prebuilt dashboard is available:

- 📄 [dashboard.json](dashboard.json)

To import:
1. Open Grafana → Dashboards → Import
2. Paste the JSON or upload the file
3. Select Prometheus data source

---

## 📊 Monitoring & Logging URLs

| Purpose            | URL                                                                                    | Login              |
| ------------------ | -------------------------------------------------------------------------------------- | ------------------ |
| **Grafana**        | [http://localhost:3000](http://localhost:3000)                                         | admin / admin      |
| **Prometheus**     | [http://localhost:9090](http://localhost:9090)                                         | No login           |
| **Kibana Logs**    | [http://localhost:5601](http://localhost:5601)                                         | No login (default) |
| **Spring Metrics** | [http://localhost:8081/actuator/prometheus](http://localhost:8081/actuator/prometheus) | Secured (JWT)      |

> In Grafana, connect Prometheus and import dashboard panels manually or use PromQL like:

```promql
rate(task_retry_count_total[1m])
histogram_quantile(0.95, rate(task_execution_duration_ms_bucket[5m]))
```

> In Kibana, go to:

```
Stack Management > Data Views > Create index pattern: spring-boot-logs*
```

