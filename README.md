# Automated Attendance System

A Spring Boot Java application for managing student attendance using facial recognition and web dashboards for faculty and students.

## Project Overview

This project automates classroom attendance marking using computer vision. Faculty members can start a face-recognition attendance session using a web camera, manage class rosters, update attendance manually, and download class reports as PDF files. Students can log in to check their overall and subject-wise attendance percentages.

## Features

- **Facial Recognition Attendance**: Automatically identifies students and records attendance using OpenCV / JavaCV.
- **Faculty Dashboard**:
  - Take attendance using live camera face recognition.
  - View class rosters and modify student attendance statuses (Present/Absent).
  - Export class attendance reports to PDF.
- **Student Dashboard**:
  - Track overall attendance percentage across all subjects.
  - View detailed attendance logs and subject breakdowns.
- **Authentication System**: Separate login access for faculty members and students, with password reset support.
- **Database**: Embedded SQLite database managed through Spring Data JPA and Hibernate.

## Technologies Used

- **Java 17** & **Spring Boot 3.2**
- **Thymeleaf**, HTML5, CSS3, JavaScript
- **JavaCV / OpenCV** (Face processing & recognition)
- **SQLite** & **Spring Data JPA**
- **OpenPDF** (PDF export generation)
- **Maven** (Build and dependency management)

## Project Structure

```text
AutoSystem-Java/
├── pom.xml
└── src/
    └── main/
        ├── java/com/autosystem/
        │   ├── AutoSystemApplication.java
        │   ├── controller/      # Web controllers (Auth, Faculty, Student, Api)
        │   ├── model/           # Data entities (Student, Faculty, Attendance, Subject)
        │   ├── repository/      # JPA database access layer
        │   └── service/         # Services (Face Recognition, Attendance, PDF Export)
        └── resources/
            ├── application.properties
            ├── static/css/      # Custom styles
            └── templates/       # HTML views (Dashboards, Login, Register)
```

## How to Run

### Prerequisites
- Installed **JDK 17** or higher
- Installed **Maven**

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/roshinikouser-debug/Atuomated-attendnce-system-.git
   cd Atuomated-attendnce-system-
   ```

2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

3. Open your browser and navigate to:
   `http://localhost:8080`

## Author

Developed by **Roshini Kouser**
