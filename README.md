# 📸 Automated Attendance System (Spring Boot + OpenCV)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenCV](https://img.shields.io/badge/JavaCV%2FOpenCV-1.5.10-blue.svg)](https://bytedeco.org/)
[![Database](https://img.shields.io/badge/SQLite-3.45-lightgrey.svg)](https://www.sqlite.org/)
[![PDF Export](https://img.shields.io/badge/OpenPDF-1.3.37-red.svg)](https://github.com/LibrePDF/OpenPDF)

A full-stack, AI-powered **Automated Attendance System** built using **Java 17**, **Spring Boot 3**, **Thymeleaf**, **OpenCV/JavaCV**, and **SQLite**. The system automates attendance tracking through facial recognition technology while offering comprehensive dashboards for both Faculty and Students.

---

## ✨ Features

- 🤖 **AI Facial Recognition Engine**: Integrated **JavaCV / OpenCV** engine for automated biometric face identification and attendance marking.
- 👨‍🏫 **Faculty Portal**:
  - Live face-recognition attendance taking session.
  - Manual attendance marking & status overrides (Present / Absent / Late).
  - Subject enrollment management & student rosters.
  - Automated PDF report generation & analytics download.
- 🎓 **Student Portal**:
  - Personal dashboard displaying overall & subject-wise attendance percentages.
  - Real-time breakdown of attended vs. missed lectures.
  - Attendance history log.
- 📄 **PDF Report Generation**: Instant export of class attendance sheets powered by **OpenPDF**.
- 🔐 **Authentication & Security**:
  - Role-based login system for **Faculty** and **Students**.
  - Password reset mechanism with email token generation.
- 💾 **Zero-Config Database**: Embedded **SQLite** database managed via **Spring Data JPA** & **Hibernate**.

---

## 🛠️ Tech Stack

| Domain | Technology |
| :--- | :--- |
| **Backend Framework** | Java 17, Spring Boot 3.2.3 |
| **Frontend / Templates** | Thymeleaf, HTML5, CSS3, JavaScript |
| **Computer Vision / AI** | JavaCV / OpenCV 1.5.10 |
| **Database & ORM** | SQLite JDBC, Spring Data JPA, Hibernate 6 |
| **PDF Generation** | OpenPDF 1.3.37 |
| **Build Tool** | Apache Maven |

---

## 📂 Project Structure

```text
AutoSystem-Java/
├── pom.xml
├── .gitignore
├── README.md
├── .mvn/
└── src/
    ├── main/
    │   ├── java/com/autosystem/
    │   │   ├── AutoSystemApplication.java        # Main Spring Boot Entrypoint
    │   │   ├── controller/                      # Web Controllers
    │   │   │   ├── ApiController.java
    │   │   │   ├── AuthController.java
    │   │   │   ├── CustomErrorController.java
    │   │   │   ├── FacultyController.java
    │   │   │   └── StudentController.java
    │   │   ├── model/                           # JPA Entities
    │   │   │   ├── Attendance.java
    │   │   │   ├── Faculty.java
    │   │   │   ├── PasswordResetToken.java
    │   │   │   ├── Student.java
    │   │   │   └── SubjectEnrollment.java
    │   │   ├── repository/                      # Data Repositories
    │   │   │   ├── AttendanceRepository.java
    │   │   │   ├── FacultyRepository.java
    │   │   │   ├── PasswordResetTokenRepository.java
    │   │   │   ├── StudentRepository.java
    │   │   │   └── SubjectEnrollmentRepository.java
    │   │   └── service/                         # Business Logic & AI Engines
    │   │       ├── AttendanceService.java
    │   │       ├── DataInitializerService.java
    │   │       ├── FaceEngineService.java       # OpenCV Biometric Processing
    │   │       └── PdfExportService.java        # PDF Export Engine
    │   └── resources/
    │       ├── application.properties           # App & SQLite Configuration
    │       ├── static/css/style.css             # UI Stylesheet
    │       └── templates/                       # Thymeleaf Web UI Views
    │           ├── base.html
    │           ├── faculty_dashboard.html
    │           ├── student_dashboard.html
    │           ├── login.html
    │           ├── register.html
    │           ├── forgot_password.html
    │           └── reset_password.html
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed on your machine:
- **JDK 17** or higher (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Git**

### Installation & Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/roshinikouser-debug/Atuomated-attendnce-system-.git
   cd Atuomated-attendnce-system-
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run the Spring Boot Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the Application**:
   Open your browser and navigate to:
   ```text
   http://localhost:8080
   ```

---

## 🔒 Default Credentials & Initial Data

Upon first startup, `DataInitializerService` automatically creates initial demo records:
- **Faculty Login**: Default faculty account automatically seeded.
- **Student Login**: Default student account automatically seeded.

---

## 📄 License & Author

Developed with ❤️ by **[Roshini Kouser](https://github.com/roshinikouser-debug)**.
