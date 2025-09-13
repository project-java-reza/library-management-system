package com.sinaukoding.librarymanagementsystem.repository.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusBukuRepository extends JpaRepository<StatusBuku, String> {
    Optional<StatusBuku> findByStatusBuku(EStatusBuku statusBuku);
}
