package com.autosystem.service;

import com.autosystem.model.Attendance;
import com.autosystem.model.Student;
import com.autosystem.repository.AttendanceRepository;
import com.autosystem.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public void markAttendance(Set<Long> detectedStudentIds, String subjectName, Long facultyId) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Student> allStudents = studentRepository.findAll();

        if (allStudents.isEmpty()) {
            System.out.println("[WARN] No students registered in the system.");
            return;
        }

        for (Student student : allStudents) {
            Optional<Attendance> existingOpt = attendanceRepository.findByStudentIdAndSubjectNameAndDate(
                    student.getId(), subjectName, date);

            String status = detectedStudentIds.contains(student.getId()) ? "Present" : "Absent";

            if (existingOpt.isPresent()) {
                Attendance existing = existingOpt.get();
                if (!"Present".equalsIgnoreCase(existing.getStatus()) && "Present".equals(status)) {
                    existing.setStatus("Present");
                    existing.setFacultyId(facultyId);
                    attendanceRepository.save(existing);
                }
            } else {
                Attendance record = new Attendance(student.getId(), facultyId, subjectName, date, status);
                attendanceRepository.save(record);
            }
        }
        System.out.println("[INFO] Attendance saved for subject: " + subjectName + " on " + date);
    }

    @Transactional
    public void markSingleAttendance(Long studentId, String subjectName, Long facultyId, String status) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<Attendance> existingOpt = attendanceRepository.findByStudentIdAndSubjectNameAndDate(
                studentId, subjectName, date);

        if (existingOpt.isPresent()) {
            Attendance existing = existingOpt.get();
            existing.setStatus(status);
            existing.setFacultyId(facultyId);
            attendanceRepository.save(existing);
        } else {
            Attendance record = new Attendance(studentId, facultyId, subjectName, date, status);
            attendanceRepository.save(record);
        }
        System.out.println("[INFO] Manual attendance (" + status + ") saved for student ID " + studentId + ", subject: " + subjectName);
    }
}
