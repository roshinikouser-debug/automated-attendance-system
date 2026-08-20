package com.autosystem.controller;

import com.autosystem.model.Attendance;
import com.autosystem.repository.AttendanceRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class StudentController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/student_dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("student_id");
        if (studentId == null) {
            return "redirect:/";
        }

        Map<String, Map<String, Object>> subjectStats = new LinkedHashMap<>();
        long totalOverall = 0;
        long presentOverall = 0;

        for (String sub : AuthController.OFFICIAL_SUBJECTS) {
            long total = attendanceRepository.countByStudentIdAndSubjectName(studentId, sub);
            long present = attendanceRepository.countByStudentIdAndSubjectNameAndStatus(studentId, sub, "Present");

            if (total > 0) {
                double pct = Math.round((present * 100.0 / total) * 10.0) / 10.0;
                Map<String, Object> statMap = new HashMap<>();
                statMap.put("total", total);
                statMap.put("present", present);
                statMap.put("percentage", pct);
                subjectStats.put(sub, statMap);

                totalOverall += total;
                presentOverall += present;
            }
        }

        double overallPct = totalOverall > 0 ? Math.round((presentOverall * 100.0 / totalOverall) * 10.0) / 10.0 : 0.0;

        List<Attendance> records = attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
        Map<String, List<Attendance>> attendanceByDate = new LinkedHashMap<>();
        for (Attendance rec : records) {
            attendanceByDate.computeIfAbsent(rec.getDate(), k -> new ArrayList<>()).add(rec);
        }

        model.addAttribute("stats", subjectStats);
        model.addAttribute("overall_pct", overallPct);
        model.addAttribute("attendance_by_date", attendanceByDate);

        return "student_dashboard";
    }
}
