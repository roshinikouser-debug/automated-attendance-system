package com.autosystem.repository;

import com.autosystem.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    Optional<Attendance> findByStudentIdAndSubjectNameAndDate(Long studentId, String subjectName, String date);

    long countByStudentIdAndSubjectName(Long studentId, String subjectName);
    long countByStudentIdAndSubjectNameAndStatus(Long studentId, String subjectName, String status);

    @Query("SELECT COUNT(DISTINCT a.date) FROM Attendance a WHERE a.subjectName = :subjectName")
    long countDistinctDatesBySubjectName(@Param("subjectName") String subjectName);

    @Query("SELECT COUNT(DISTINCT a.date) FROM Attendance a WHERE a.subjectName IN :subjectNames")
    long countDistinctDatesBySubjectNames(@Param("subjectNames") List<String> subjectNames);
}
