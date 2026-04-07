# Pokhara University Exam Routine Generator

A desktop application that automatically generates conflict-free examination timetables for **Pokhara University (PU)**, Nepal. It models course-student relationships as a graph and uses a **graph coloring algorithm** to assign exam slots so that no student is scheduled for two exams at the same time.

---

## Features

- Automated exam schedule generation using graph coloring
- Supports both **regular** (semester) and **back-paper** (re-sit) exams
- Configurable minimum/maximum gap between successive exam days
- Full academic hierarchy: Faculty → Level → Program → Semester → Course
- Distributed scheduling via **Java RMI** — offload computation to a remote server
- Live database connection reconfiguration without restart
- Swing-based GUI with tree views and calendar visualization

---

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java SE |
| UI | Java Swing |
| Database | MySQL (JDBC) |
| Build | Apache Ant (NetBeans project) |
| Remoting | Java RMI |
| Algorithm | Graph Coloring |

---

## Prerequisites

- JDK 8 or later
- MySQL Server
- MySQL JDBC Driver (`mysql-connector-java`)
- Apache Ant (or open directly in NetBeans IDE)

---

## Database Setup

1. Start your MySQL server.
2. Create a database named `puroutine`.
3. Import the schema from the `db_db/puroutine/` directory.
4. The default connection settings are:

   | Setting | Default |
   |---|---|
   | URL | `jdbc:mysql://127.0.0.1:3306/` |
   | Database | `puroutine` |
   | Username | `root` |
   | Password | `root` |

   Connection settings can be changed at runtime from the **Database** panel in the application.

---

## Building & Running

### Using NetBeans IDE

1. Open the project folder in NetBeans.
2. Right-click the project → **Clean and Build**.
3. Click **Run** (or press `F6`).

### Using Ant (command line)

```bash
ant clean build
java -jar dist/PUExamRoutine.jar
```

### Running as a Remote Scheduling Server

To offload graph coloring computation to a dedicated machine:

```bash
java -jar dist/PUExamRoutine.jar <port>
```

Replace `<port>` with the desired RMI port (e.g., `3232`). Clients connect via `ExamConnector` using the server's host and port.

---

## Project Structure

```
src/puexamroutine/
├── Main.java                  # Entry point; starts UI or RMI server
├── control/
│   ├── Controller.java        # Central application controller
│   ├── database/              # JDBC connection, read, write
│   ├── domain/                # Domain model (Course, Program, Exam, Centre…)
│   ├── routinegeneration/     # Graph coloring algorithm & RMI server
│   └── schedule/              # Exam scheduler (date assignment)
└── ui/                        # Swing forms and panels
```

---

## How It Works

1. **Data Loading** — Student enrollment and course data are read from the MySQL database.
2. **Graph Construction** — Courses that share at least one common student are connected by an edge in a conflict graph.
3. **Graph Coloring** — Each color represents a unique exam slot. The coloring algorithm ensures no two adjacent (conflicting) courses share the same slot.
4. **Date Assignment** — Colored slots are mapped to actual calendar dates respecting the configured gap between exam days.
5. **Output** — The generated routine is displayed in tree and calendar views and can be reviewed per-program or per-centre.

---

## Author

**Sumit Shrestha**
