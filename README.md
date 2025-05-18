
# Task Management System (TMS)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)

A web-based task management application built with Java Servlets, JSP, and PostgreSQL, designed to help teams organize and track their work efficiently.

## Features

- **Task Management**:
  - Create, view, edit, and delete tasks
  - Set priorities (High/Medium/Low)
  - Track status (Pending/In Progress/Completed/Failed)
  - Add due dates, descriptions and tags

- **Organization**:
  - Tag tasks with custom labels
  - Filter tasks by status
  - Sort tasks by due date

- **User Experience**:
  - Responsive web interface
  - Form validation
  - Intuitive task visualization

## Technologies Used

- **Backend**: Java Servlets
- **Frontend**: JSP, Bootstrap 5
- **Database**: PostgreSQL
- **Application Server**: Apache Tomcat 10.x
- **Build Tool**: Maven

## System Requirements

- Java JDK 17 or higher
- Apache Tomcat 10.x
- PostgreSQL 14 or higher
- Maven 3.8+ (for building)

## Installation

### 1. Database Setup

```bash
# Create database (run in psql)
CREATE DATABASE taskdb;

# Connect and run schema script
\c tms
\i src/main/resources/db/schema.sql
```

### 2. Configuration

Create `application.properties` in `src/main/resources`:

```properties
# Database configuration
db.url=jdbc:postgresql://localhost:5432/tms
db.username=your_username
db.password=your_password
db.driver=org.postgresql.Driver

# Application settings
date.format=yyyy-MM-dd
```

## Usage

1. Start Tomcat server
2. Access application at: `http://localhost:8080/tasks`
3. Default test data is automatically loaded

## API Endpoints

| Method | Path          | Description                     |
|--------|---------------|---------------------------------|
| GET    | /tasks        | List all tasks                  |
| GET    | /tasks?action=new | Show new task form          |
| GET    | /tasks?action=edit&id={id} | Show edit form |
| POST   | /tasks        | Create/Update task              |
| GET    | /tasks?action=delete&id={id} | Delete task |
| GET    | /tasks?action=filter&status={status} | Filter by status |
| GET    | /tasks?action=sort&order={asc/desc} | Sort by due date |

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── tms/
|   |       |-- config/       # Database config.
│   │       ├── controller/   # Servlets
│   │       ├── dao/          # Database access
|   |       |-- exception/    # Custom exceptions
│   │       ├── model/        # Domain objects
│   │       ├── service/      # Business logic
│   │       └── util/         # Utilities
│   ├── resources/            # Config files
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── jsp/          # View templates
│       │   └── web.xml       # Deployment descriptor
│       ├── css/              # Stylesheets
│       └── js/               # JavaScript
└── test/                     # Unit tests
```

## Screenshots

![Task List](screenshots/task-list.png)
*Task Dashboard with filtering options*

![Task Form](screenshots/task-form.png)
*Task creation/editing form*

