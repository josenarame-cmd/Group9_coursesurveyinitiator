# Course Evaluation Survey System

A web-based Course Evaluation Survey System built with **Spring MVC**, **JSP/JSTL**, and **MySQL**.

## Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 9 (or use the embedded Tomcat Maven plugin)

### 1. Database Setup
```sql
mysql -u root -p < database/schema.sql
```

### 2. Configure Database & Mail
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/course_eval_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=yourpassword

mail.host=smtp.gmail.com
mail.port=587
mail.username=your@gmail.com
mail.password=your-app-password
```

### 3. Build & Run
```bash
mvn clean package
mvn tomcat7:run
```

Open: [http://localhost:8080/courseeval](http://localhost:8080/courseeval)

### Default Admin Account
| Field    | Value    |
|----------|----------|
| Username | `admin`  |
| Password | `Admin@123` |

## Project Structure
```
src/
├── main/
│   ├── java/com/courseeval/
│   │   ├── controller/   # Spring MVC Controllers
│   │   ├── model/        # POJOs / Domain Objects
│   │   ├── dao/          # Data Access (JdbcTemplate)
│   │   └── service/      # Email Service
│   ├── resources/
│   │   └── db.properties
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   ├── spring-mvc-config.xml
│       │   ├── applicationContext.xml
│       │   └── views/           # JSP files
│       ├── css/style.css
│       └── js/main.js
└── database/
    └── schema.sql
```

## Roles
| Role       | Default Access          |
|------------|-------------------------|
| ADMIN      | Manage users/courses    |
| INITIATOR  | Create & manage surveys |
| TEACHER    | View surveys & results  |
| RESPONDENT | Take surveys            |
