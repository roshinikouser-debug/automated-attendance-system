package com.autosystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String image;

    @Column(name = "class_name", nullable = true)
    private String className;

    @Column(name = "roll_number", nullable = true)
    private String rollNumber;

    // Transient attributes for template stats rendering
    @Transient
    private String displayStats;

    @Transient
    private double attendancePct;

    public Student() {}

    public Student(String name, String email, String password, String image, String className, String rollNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.className = className;
        this.rollNumber = rollNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDisplayStats() { return displayStats; }
    public void setDisplayStats(String displayStats) { this.displayStats = displayStats; }

    public double getAttendancePct() { return attendancePct; }
    public void setAttendancePct(double attendancePct) { this.attendancePct = attendancePct; }
}
