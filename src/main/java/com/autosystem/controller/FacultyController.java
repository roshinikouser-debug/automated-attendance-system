package com.autosystem.controller;

import com.autosystem.model.Attendance;
import com.autosystem.model.Student;
import com.autosystem.model.SubjectEnrollment;
import com.autosystem.repository.AttendanceRepository;
import com.autosystem.repository.StudentRepository;
import com.autosystem.repository.SubjectEnrollmentRepository;
import com.autosystem.service.AttendanceService;
import com.autosystem.service.FaceEngineService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.autosystem.service.DataInitializerService;
import com.autosystem.service.PdfExportService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FacultyController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SubjectEnrollmentRepository subjectEnrollmentRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private FaceEngineService faceEngineService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private DataInitializerService dataInitializerService;

    @Value("${autosystem.upload.dir:static/uploads}")
    private String uploadDir;

    @GetMapping("/faculty_dashboard")
    public String facultyDashboard(@RequestParam(required = false, defaultValue = "") String search,
                                   @RequestParam(required = false, defaultValue = "") String class_name,
                                   @RequestParam(required = false, defaultValue = "All Subjects") String reg_subject,
                                   HttpSession session, Model model) {

        Long facultyId = (Long) session.getAttribute("faculty_id");
        if (facultyId == null) {
            return "redirect:/";
        }

        List<Long> enrolledStudentIds = subjectEnrollmentRepository.findDistinctStudentIds();
        List<Student> students = studentRepository.findAllById(enrolledStudentIds);

        // Filter search
        if (!search.isBlank()) {
            String searchLower = search.toLowerCase();
            students = students.stream()
                    .filter(s -> s.getName().toLowerCase().contains(searchLower) || s.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Filter class
        if (!class_name.isBlank()) {
            students = students.stream()
                    .filter(s -> class_name.equalsIgnoreCase(s.getClassName()))
                    .collect(Collectors.toList());
        }

        // Filter subject registration
        if (reg_subject != null && !"All Subjects".equalsIgnoreCase(reg_subject)) {
            List<Long> subjectEnrolledIds = subjectEnrollmentRepository.findBySubjectName(reg_subject)
                    .stream().map(SubjectEnrollment::getStudentId).collect(Collectors.toList());
            students = students.stream()
                    .filter(s -> subjectEnrolledIds.contains(s.getId()))
                    .collect(Collectors.toList());
        }

        // Calculate statistics
        for (Student s : students) {
            if (reg_subject != null && !"All Subjects".equalsIgnoreCase(reg_subject)) {
                long totalHeld = attendanceRepository.countDistinctDatesBySubjectName(reg_subject);
                long present = attendanceRepository.countByStudentIdAndSubjectNameAndStatus(s.getId(), reg_subject, "Present");
                s.setDisplayStats(present + " / " + totalHeld);
                s.setAttendancePct(totalHeld > 0 ? Math.round((present * 100.0 / totalHeld) * 10.0) / 10.0 : 0.0);
            } else {
                List<Attendance> records = attendanceRepository.findByStudentId(s.getId());
                long totalPresent = records.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
                List<String> theirSubjects = subjectEnrollmentRepository.findByStudentId(s.getId())
                        .stream().map(SubjectEnrollment::getSubjectName).collect(Collectors.toList());
                long totalAvailable = theirSubjects.isEmpty() ? 0 : attendanceRepository.countDistinctDatesBySubjectNames(theirSubjects);
                s.setDisplayStats(totalPresent + " / " + totalAvailable);
                s.setAttendancePct(totalAvailable > 0 ? Math.round((totalPresent * 100.0 / totalAvailable) * 10.0) / 10.0 : 0.0);
            }
        }

        List<String> uniqueClasses = studentRepository.findDistinctClassNames();

        Map<String, Long> subjectSessions = new HashMap<>();
        for (String sub : AuthController.OFFICIAL_SUBJECTS) {
            subjectSessions.put(sub, attendanceRepository.countDistinctDatesBySubjectName(sub));
        }

        model.addAttribute("students", students);
        model.addAttribute("unique_classes", uniqueClasses);
        model.addAttribute("all_subjects", AuthController.OFFICIAL_SUBJECTS);
        model.addAttribute("subject_sessions", subjectSessions);
        model.addAttribute("current_search", search);
        model.addAttribute("current_class", class_name);
        model.addAttribute("current_reg_subject", reg_subject);

        return "faculty_dashboard";
    }

    @PostMapping("/start_attendance")
    public String startAttendance(@RequestParam String subject_name,
                                  @RequestParam Long student_id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Long facultyId = (Long) session.getAttribute("faculty_id");
        if (facultyId == null) {
            return "redirect:/";
        }

        Optional<Student> targetOpt = studentRepository.findById(student_id);
        if (targetOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "System Error: Target student not found.");
            return "redirect:/faculty_dashboard";
        }

        Student student = targetOpt.get();
        Optional<SubjectEnrollment> enrollment = subjectEnrollmentRepository.findByStudentIdAndSubjectName(student_id, subject_name);
        if (enrollment.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Access Denied: Student is not enrolled in " + subject_name + ". Enroll first.");
            return "redirect:/faculty_dashboard";
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (attendanceRepository.findByStudentIdAndSubjectNameAndDate(student_id, subject_name, today).isPresent()) {
            redirectAttributes.addFlashAttribute("warning", "no multiple scan are allowed for this subject today.");
            return "redirect:/faculty_dashboard";
        }

        boolean verified = faceEngineService.verifyStudentFace(student, uploadDir);
        if (verified) {
            attendanceService.markAttendance(Collections.singleton(student_id), subject_name, facultyId);
            redirectAttributes.addFlashAttribute("success", "Verification Successful: " + student.getName() + "'s attendance has been logged for " + subject_name + ".");
        } else {
            redirectAttributes.addFlashAttribute("info", "Verification Incomplete: No match was recorded.");
        }

        return "redirect:/faculty_dashboard";
    }

    @PostMapping("/mark_manual")
    public String markManual(@RequestParam Long student_id,
                             @RequestParam String subject_name,
                             @RequestParam(defaultValue = "Present") String status,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Long facultyId = (Long) session.getAttribute("faculty_id");
        if (facultyId == null) {
            return "redirect:/";
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (attendanceRepository.findByStudentIdAndSubjectNameAndDate(student_id, subject_name, today).isPresent()) {
            redirectAttributes.addFlashAttribute("warning", "no multiple scan are allowed for this subject today.");
            return "redirect:/faculty_dashboard";
        }

        if (subjectEnrollmentRepository.findByStudentIdAndSubjectName(student_id, subject_name).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Access Denied: Student is not officially registered for " + subject_name + ".");
            return "redirect:/faculty_dashboard";
        }

        attendanceService.markSingleAttendance(student_id, subject_name, facultyId, status);
        redirectAttributes.addFlashAttribute("success", "Success: Attendance recorded for " + subject_name + ".");
        return "redirect:/faculty_dashboard";
    }

    @GetMapping("/export_attendance")
    public void exportAttendance(@RequestParam(required = false, defaultValue = "") String search,
                                 @RequestParam(required = false, defaultValue = "") String class_name,
                                 HttpSession session,
                                 HttpServletResponse response) throws IOException {

        Long facultyId = (Long) session.getAttribute("faculty_id");
        if (facultyId == null) {
            response.sendRedirect("/");
            return;
        }

        List<Student> students = studentRepository.findAll();
        if (!search.isBlank()) {
            String sLower = search.toLowerCase();
            students = students.stream().filter(s -> s.getName().toLowerCase().contains(sLower) || s.getEmail().toLowerCase().contains(sLower)).collect(Collectors.toList());
        }
        if (!class_name.isBlank()) {
            students = students.stream().filter(s -> class_name.equalsIgnoreCase(s.getClassName())).collect(Collectors.toList());
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_report.csv");

        PrintWriter writer = response.getWriter();
        writer.println("Name,Email,Class,Roll Number,Total Classes,Present,Attendance %");

        for (Student s : students) {
            List<Attendance> records = attendanceRepository.findByStudentId(s.getId());
            long total = records.size();
            long present = records.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
            double pct = total > 0 ? Math.round((present * 100.0 / total) * 10.0) / 10.0 : 0.0;

            writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%.1f%%",
                    s.getName(), s.getEmail(), s.getClassName() != null ? s.getClassName() : "",
                    s.getRollNumber() != null ? s.getRollNumber() : "", total, present, pct));
        }
        writer.flush();
    }

    @GetMapping("/export_pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        File mdFile = new File("../research_paper.md");
        if (!mdFile.exists()) {
            mdFile = new File("../AutoSystem/research_paper.md");
        }
        if (!mdFile.exists()) {
            mdFile = new File("research_paper.md");
        }

        byte[] pdfBytes;
        if (mdFile.exists()) {
            pdfBytes = pdfExportService.generateIeeePdfFromMarkdown(mdFile);
        } else {
            pdfBytes = "AutoSystem IEEE Research Paper".getBytes();
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=AutoSystem_IEEE_Research_Paper.pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @PostMapping("/reset_system")
    public String resetSystem(RedirectAttributes redirectAttributes) {
        dataInitializerService.resetSystem();
        redirectAttributes.addFlashAttribute("success", "Full System Wipe successful. Refresh your dashboard.");
        return "redirect:/";
    }
}
