package com.autosystem.controller;

import com.autosystem.model.SubjectEnrollment;
import com.autosystem.repository.SubjectEnrollmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class ApiController {

    @Autowired
    private SubjectEnrollmentRepository subjectEnrollmentRepository;

    @GetMapping("/check_registration_api")
    public ResponseEntity<Map<String, Object>> checkRegistration(@RequestParam Long student_id,
                                                                 @RequestParam String subject_name,
                                                                 HttpSession session) {

        if (session.getAttribute("faculty_id") == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        Map<String, Object> res = new HashMap<>();
        Optional<SubjectEnrollment> enrollment = subjectEnrollmentRepository.findByStudentIdAndSubjectName(student_id, subject_name);

        if (enrollment.isPresent()) {
            res.put("registered", true);
            res.put("message", "Access Granted: Registered");
        } else {
            res.put("registered", false);
            res.put("message", "Access Denied: Not Registered");
        }

        return ResponseEntity.ok(res);
    }
}
