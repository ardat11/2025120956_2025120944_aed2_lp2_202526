# 🎬 Streaming Platform Data Management & Recommendation System

A high-performance, university-grade Java database and graph recommendation platform built on robust Object-Oriented Programming (OOP) principles, Princeton Algorithms (`algs4`), and JavaFX GUI. 

This project integrates high-speed symbol tables (Linear Probing Hashing), balanced search trees (Red-Black BSTs), and directed weighted graphs to manage users, artists, contents, and genres, offering advanced range queries and structural shortest-path algorithms.

---

## 👥 Authors
* **Student ID 1:** `2025120956`
* **Student ID 2:** `2025120944`
* **Course:** Algorithms & Data Structures II (AED2) & Object-Oriented Programming (LP2)

---

## ⚙️ Technology Stack
* **Java SDK:** `25` (OpenJDK)
* **GUI Framework:** JavaFX `17.0.12`
* **Build System:** Maven `3.8.5`
* **Algorithms Core Library:** Princeton `algs4`

---

## 🏛️ Core Architectural Highlights

### 1. Hybrid Storage Engine
* **Primary Key Indexing ($\Theta(1)$):** Utilizes `LinearProbingHashST` and `SeparateChainingHashST` to register, retrieve, and delete entities (`User`, `Artist`, `Content`, `Genre`) in constant time.
* **Balanced Search Trees ($O(\log N)$):** Maintains multiple `RedBlackBST` indexes mapped to `SET` collections for high-speed name substring searches, prefix matching, and temporal range queries.

### 2. Directed Weighted Graph Model (Phase 2 backend)
* Represents complex social networks and viewing paths using a custom directed graph layer extending Princeton's `DirectedEdge` and `EdgeWeightedDigraph`.
* Supports:
  * **Shortest Pathfinding ($O(E \log V)$):** Multi-criteria Dijkstra search paths (shortest social follow chain, lowest-cost film watches).
  * **Structural Recommendations:** Friend recommendation paths based on structural proximity.
  * **Graph Connectivity ($O(V + E)$):** connectivity checks via BFS/DFS.
  * **Subgraphs:** Extraction of specific genre or rating subgraphs.

### 3. Symmetrical Double Persistence
* **CSV Flat-File Backups (R10):** Standard semicolon (`;`) delimited text loaders and writers with automated BOM character stripping (`\uFEFF`) and clean format parsers.
* **Binary Snapshots (R11):** Native object stream serialization using `ObjectOutputStream` and `ObjectInputStream`.
* **Referential Re-linking (Reference Duplication Fix):** During deserialization, the loader automatically resolves duplicate memory references and re-links content entities to the single official `Genre` and `Artist` instances in the database tables, guaranteeing **100% Referential Integrity**.

### 4. Integrity Cascades & Archiving
* **Deleted Entities Archive (R13):** Deleted users and artists are pushed onto a LIFO Stack archive, allowing retrieval or export.
* **Cascading Deletions:** Deleting a user or content automatically cleans all associated relationship edges from the background graph and secondary indexing trees, avoiding null pointer exceptions.

---

## 🚀 Execution & Setup Guide

Ensure your shell has its `JAVA_HOME` pointing to JDK 25 (or set it manually in your shell, e.g., `$env:JAVA_HOME = "C:\path\to\jdk-25"`).

### 1. Compilation
Clean build and compile the Maven project:
```powershell
.\mvnw.cmd clean compile
```

### 2. Execute Master Test Runner
Execute the pre-configured backend verification test suite (testing CRUD, serialization, and graphs):
```powershell
.\mvnw.cmd compile exec:java "-Dexec.mainClass=testing.TestRunner"
```

### 3. Generate Javadoc HTML Documentation
Generate comprehensive API Javadoc documentation pages under the `target/reports/apidocs` directory:
```powershell
.\mvnw.cmd javadoc:javadoc
```

### 4. Launch the JavaFX Application
You can run the JavaFX GUI directly via Maven:
```powershell
.\mvnw.cmd compile javafx:run
```
*Alternatively, you can run the [Launcher.java](src/main/java/com/example/_025120956_2025120944_aed2_lp2_202526/Launcher.java) main method inside your IDE (e.g. IntelliJ IDEA) to bypass standard JavaFX module path runtime warnings.*

---

## ⏱️ Computational Complexities

The following table summarizes the worst-case time complexities of our core database and engine operations:

| Operations / Features | Backend Data Structure | Time Complexity (Average/Worst) |
| :--- | :--- | :--- |
| **Insert / Retrieve Entity** | `LinearProbingHashST` | $\Theta(1)$ / $O(N)$ |
| **Delete / Archive Entity** | `SeparateChainingHashST` + LIFO Stack | $\Theta(1)$ / $O(N)$ |
| **Range / Date Queries** | `RedBlackBST` Range Search | $O(\log N + K)$ ($K$ = match size) |
| **Prefix Name Match** | `RedBlackBST` Substring Lookup | $O(\log N + K)$ |
| **Dijkstra Shortest Path** | `EdgeWeightedDigraph` + `MinPQ` | $O(E \log V)$ |
| **Graph Connectivity** | Depth-First Search (`DFS`) | $O(V + E)$ |
| **Binary Backup Save** | `ObjectOutputStream` | $O(N)$ |
| **Binary Restore & Link** | `ObjectInputStream` + ST Re-Link | $O(N \log M)$ |

---

## 📂 Project Package Structure

```text
├── data/                            # Text and binary backup files (.txt, .bin)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/example/.../     # JavaFX UI layer (StreamingApplication, StreamingController)
│   │   │   ├── database/            # Database and Persistence (StreamingDatabase, FileManager, Archive)
│   │   │   ├── models/              # Domain Models (Entity, User, Artist, Content, Genre, RelationshipEdge)
│   │   │   ├── services/            # Services (SearchEngine, GraphService)
│   │   │   └── testing/             # Test Suites (TestRunner, FileTest, DatabaseTest, GraphTest)
│   │   └── resources/
│   │       └── com/example/.../     # FXML UI Layout files (streaming-view.fxml)
├── pom.xml                          # Maven build dependencies and plugins configuration
└── README.md                        # Documentation overview
```