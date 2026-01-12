package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeminjamanBukuRepository extends JpaRepository<PeminjamanBuku, String>, JpaSpecificationExecutor<PeminjamanBuku> {

    List<PeminjamanBuku> findByUser(User user);

    Page<PeminjamanBuku> findByUser(User user, Pageable pageable);
}
