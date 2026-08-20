package com.autosystem.service;

import com.autosystem.model.Faculty;
import com.autosystem.model.Student;
import com.autosystem.model.SubjectEnrollment;
import com.autosystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializerService implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SubjectEnrollmentRepository subjectEnrollmentRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${autosystem.upload.dir:static/uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public void run(String... args) {
        seedInitialData();
    }

    public void seedInitialData() {
        // Ensure demo faculty kumar@gmail.com exists
        if (facultyRepository.findByEmail("kumar@gmail.com").isEmpty()) {
            Faculty fac1 = new Faculty("Mr. Kumar", "kumar@gmail.com", "123");
            Faculty fac2 = new Faculty("Ms. Devi", "devi@gmail.com", "123");
            facultyRepository.saveAll(Arrays.asList(fac1, fac2));
            System.out.println("[INFO] Seeded default faculty accounts (kumar@gmail.com).");
        }

        // Ensure demo student ravi@gmail.com exists with subject enrollments
        if (studentRepository.findByEmail("ravi@gmail.com").isEmpty()) {
            List<String> defaultSubjects = Arrays.asList(
                    "Mathematics", "Physics", "Chemistry",
                    "Computer Science", "Biology", "History",
                    "English", "Geography"
            );

            List<Student> students = Arrays.asList(
                    new Student("Ravi", "ravi@gmail.com", "123", "ravi.jpg", "10A", "R101"),
                    new Student("Asha", "asha@gmail.com", "123", "asha.jpg", "10A", "R102")
            );

            for (Student s : students) {
                if (studentRepository.findByEmail(s.getEmail()).isEmpty()) {
                    Student saved = studentRepository.save(s);
                    for (String sub : defaultSubjects) {
                        subjectEnrollmentRepository.save(new SubjectEnrollment(saved.getId(), sub));
                    }
                }
            }
            System.out.println("[INFO] Seeded default student accounts (ravi@gmail.com).");
        }
    }

    @Transactional
    public void resetSystem() {
        System.out.println("--- AutoSystem Factory Reset ---");
        attendanceRepository.deleteAll();
        subjectEnrollmentRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        File dir = new File(uploadDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    }
                }
            }
        }
        System.out.println("[OK] System wipe successful.");
    }
}
