package com.autosystem.controller;

import com.autosystem.model.Faculty;
import com.autosystem.model.PasswordResetToken;
import com.autosystem.model.Student;
import com.autosystem.model.SubjectEnrollment;
import com.autosystem.repository.FacultyRepository;
import com.autosystem.repository.PasswordResetTokenRepository;
import com.autosystem.repository.StudentRepository;
import com.autosystem.repository.SubjectEnrollmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    public static final List<String> OFFICIAL_SUBJECTS = Arrays.asList(
            "Mathematics", "Physics", "Chemistry",
            "Computer Science", "Biology", "History",
            "English", "Geography", "Social Science",
            "Commerce", "Economics"
    );

    @Value("${autosystem.upload.dir:static/uploads}")
    private String uploadDir;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private SubjectEnrollmentRepository subjectEnrollmentRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String role,
                        @RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (role == null || email == null || password == null ||
                role.isBlank() || email.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Error: All fields are required.");
            return "redirect:/";
        }

        if ("student".equalsIgnoreCase(role)) {
            Optional<Student> studentOpt = studentRepository.findByEmailAndPassword(email, password);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                long enrollCount = subjectEnrollmentRepository.countByStudentId(student.getId());
                if (enrollCount > 0) {
                    session.setAttribute("student_id", student.getId());
                    redirectAttributes.addFlashAttribute("success", "Welcome back!");
                    return "redirect:/student_dashboard";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Access Denied: Your account is not yet enrolled in any academic subjects. Please contact administration.");
                    return "redirect:/";
                }
            }
        } else {
            Optional<Faculty> facultyOpt = facultyRepository.findByEmailAndPassword(email, password);
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                session.setAttribute("faculty_id", faculty.getId());
                redirectAttributes.addFlashAttribute("success", "Welcome back, Faculty!");
                return "redirect:/faculty_dashboard";
            }
        }

        redirectAttributes.addFlashAttribute("error", "Invalid Email or Password");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("all_subjects", OFFICIAL_SUBJECTS);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String role,
                           @RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String class_name,
                           @RequestParam(required = false) String roll_number,
                           @RequestParam(required = false) List<String> subjects,
                           @RequestParam(required = false) MultipartFile image,
                           RedirectAttributes redirectAttributes) {

        if (role == null || name == null || email == null || password == null ||
                role.isBlank() || name.isBlank() || email.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Error: All fields are required.");
            return "redirect:/register";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Error: Password must be at least 6 characters.");
            return "redirect:/register";
        }

        if ("student".equalsIgnoreCase(role)) {
            if (image == null || image.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Error: Please upload an image file.");
                return "redirect:/register";
            }

            if (class_name == null || roll_number == null || class_name.isBlank() || roll_number.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Error: Class Name and Roll Number are required for students.");
                return "redirect:/register";
            }

            if (subjects == null || subjects.size() != 8) {
                redirectAttributes.addFlashAttribute("error", "Mandatory: You must select exactly 8 subjects to register.");
                return "redirect:/register";
            }

            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            try {
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                File targetFile = new File(dir, filename);
                image.transferTo(targetFile);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to save uploaded image.");
                return "redirect:/register";
            }

            Student student = new Student(name, email, password, filename, class_name, roll_number);
            studentRepository.save(student);

            for (String sub : subjects) {
                SubjectEnrollment enrollment = new SubjectEnrollment(student.getId(), sub);
                subjectEnrollmentRepository.save(enrollment);
            }
        } else {
            Faculty faculty = new Faculty(name, email, password);
            facultyRepository.save(faculty);
        }

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordPage() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@RequestParam String email,
                                 @RequestParam String role,
                                 RedirectAttributes redirectAttributes) {

        if (email == null || role == null || email.isBlank() || role.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Error: Email and Role are required.");
            return "redirect:/forgot_password";
        }

        boolean exists = "student".equalsIgnoreCase(role)
                ? studentRepository.findByEmail(email).isPresent()
                : facultyRepository.findByEmail(email).isPresent();

        if (!exists) {
            redirectAttributes.addFlashAttribute("error", "Error: No account found with that email and role.");
            return "redirect:/forgot_password";
        }

        String token = UUID.randomUUID().toString();
        long expiresAt = Instant.now().getEpochSecond() + 3600;

        passwordResetTokenRepository.deleteByEmail(email);

        PasswordResetToken resetToken = new PasswordResetToken(email, role, token, expiresAt);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "/reset_password/" + token;
        redirectAttributes.addFlashAttribute("success", "Password reset link generated! Link: " + resetLink);
        return "redirect:/";
    }

    @GetMapping("/reset_password/{token}")
    public String resetPasswordPage(@PathVariable String token, Model model, RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || Instant.now().getEpochSecond() > tokenOpt.get().getExpiresAt()) {
            redirectAttributes.addFlashAttribute("error", "Error: Invalid or expired password reset link.");
            return "redirect:/";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset_password/{token}")
    public String resetPassword(@PathVariable String token,
                                @RequestParam String password,
                                @RequestParam String confirm_password,
                                RedirectAttributes redirectAttributes) {

        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || Instant.now().getEpochSecond() > tokenOpt.get().getExpiresAt()) {
            redirectAttributes.addFlashAttribute("error", "Error: Invalid or expired password reset link.");
            return "redirect:/";
        }

        if (!password.equals(confirm_password)) {
            redirectAttributes.addFlashAttribute("error", "Error: Passwords do not match.");
            return "redirect:/reset_password/" + token;
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Error: Password must be at least 6 characters.");
            return "redirect:/reset_password/" + token;
        }

        PasswordResetToken resetRecord = tokenOpt.get();
        if ("student".equalsIgnoreCase(resetRecord.getRole())) {
            studentRepository.findByEmail(resetRecord.getEmail()).ifPresent(s -> {
                s.setPassword(password);
                studentRepository.save(s);
            });
        } else {
            facultyRepository.findByEmail(resetRecord.getEmail()).ifPresent(f -> {
                f.setPassword(password);
                facultyRepository.save(f);
            });
        }

        passwordResetTokenRepository.delete(resetRecord);
        redirectAttributes.addFlashAttribute("success", "Success: Password has been successfully reset! You can now log in.");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
