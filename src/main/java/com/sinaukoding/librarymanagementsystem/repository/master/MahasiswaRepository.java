package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MahasiswaRepository extends JpaRepository<Mahasiswa, String>, JpaSpecificationExecutor<Mahasiswa> {

    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByNim(String nim);
    Optional<Mahasiswa> findByUser(User user);

    @Query("SELECT m FROM Mahasiswa m LEFT JOIN FETCH m.user WHERE m.id = :id")
    Optional<Mahasiswa> findByIdWithUser(@org.springframework.data.repository.query.Param("id") String id);

}
