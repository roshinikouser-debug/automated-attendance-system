package com.autosystem.repository;

import com.autosystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByEmailAndPassword(String email, String password);

    @Query("SELECT DISTINCT s.className FROM Student s WHERE s.className IS NOT NULL AND s.className != '' ORDER BY s.className ASC")
    List<String> findDistinctClassNames();

    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
