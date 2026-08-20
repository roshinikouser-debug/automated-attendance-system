package com.autosystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_enrollments")
public class SubjectEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    public SubjectEnrollment() {}

    public SubjectEnrollment(Long studentId, String subjectName) {
        this.studentId = studentId;
        this.subjectName = subjectName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
