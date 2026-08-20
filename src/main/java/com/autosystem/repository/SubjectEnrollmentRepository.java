package com.autosystem.repository;

import com.autosystem.model.SubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectEnrollmentRepository extends JpaRepository<SubjectEnrollment, Long> {

    List<SubjectEnrollment> findByStudentId(Long studentId);
    List<SubjectEnrollment> findBySubjectName(String subjectName);
    long countByStudentId(Long studentId);

    Optional<SubjectEnrollment> findByStudentIdAndSubjectName(Long studentId, String subjectName);

    @Query("SELECT DISTINCT e.studentId FROM SubjectEnrollment e")
    List<Long> findDistinctStudentIds();
}
