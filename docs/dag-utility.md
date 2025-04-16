
# 📘 DAG Utility Implementation – Technical Documentation

## 🎯 Objective

This implementation enables the **Distributed Task Scheduler** to:

1. Support **task dependencies** using a Directed Acyclic Graph (DAG)
2. Detect **invalid job configurations** (like cycles)
3. Compute a **topological execution order** based on dependencies

---

## 🏗️ Modifications Overview

### ✅ 1. **`Task.java` – Enhanced Model**

Added a new field:
```java
private List<String> dependencies = new ArrayList<>();
```

> This allows each task to reference other tasks by ID, forming the basis of the DAG structure.

---

### ✅ 2. **New Utility Class: `DagUtils.java`**

A reusable utility class for DAG operations:
```java
public class DagUtils {
    public static List<Task> topologicalSort(List<Task> tasks);
    public static boolean hasCycle(List<Task> tasks);
}
```

---

## 🧠 DAG Algorithms Used

### 🔹 `topologicalSort(List<Task>)`
Implements **Kahn’s Algorithm** to:

- Build an adjacency list from tasks and their dependencies
- Maintain an in-degree count for each task
- Process tasks with 0 in-degree and build the execution order

🔁 Throws `IllegalStateException` if the DAG contains a cycle.

---

### 🔹 `hasCycle(List<Task>)`
A convenience wrapper that:

- Attempts topological sort
- Returns `true` if a cycle is detected (caught via exception)

---

## 🧪 Unit Tests: `DagUtilsTest.java`

Test cases created using `JUnit 5`.

### ✔ `testTopologicalSort_NoCycle()`
- Verifies a valid linear DAG `A → B → C`
- Asserts the output order

---

### ✔ `testHasCycle()`
- Constructs a cyclic graph: `C → B → A → C`
- Confirms that the cycle is detected

---

### ✔ `testTopologicalSort_MultipleRoots()` *(Newly Added)*
- DAG with two root tasks (`A`, `B`) and a dependent (`C`)
- Asserts that `C` comes after both `A` and `B` in the sorted order

---

## 🔎 Example DAG (ASCII)

```
   Task A       Task B
     |            |
     v            v
         Task C
           |
           v
        Task D
           |
           v
        Task E
```

---

## 📂 Folder Structure (Updated)

```
src/
├── main/java/com/distributedscheduler/model/Task.java
├── main/java/com/distributedscheduler/util/DagUtils.java
└── test/java/com/distributedscheduler/util/DagUtilsTest.java
```

---

## 🔮 Future-Proof Design

This DAG utility lays the foundation for:

- 🔁 **Automatic task execution sequencing**
- ❌ **Cycle detection before task scheduling**
- 📊 **Visual tools for DAG-based monitoring**
- ⚙️ **Integration with job runners and schedulers**

---

## 📄 Deliverables

| Component             | Path                                                       |
|----------------------|------------------------------------------------------------|
| DAG Utility Class     | `src/main/java/.../util/DagUtils.java`                     |
| Unit Tests            | `src/test/java/.../util/DagUtilsTest.java`                 |
| Model Enhancement     | `src/main/java/.../model/Task.java` (added `dependencies`) |
| DAG Diagram           | Included as ASCII in docs/report                          |

