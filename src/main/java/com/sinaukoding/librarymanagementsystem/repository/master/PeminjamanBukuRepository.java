package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PeminjamanBukuRepository extends JpaRepository<PeminjamanBuku, String>, JpaSpecificationExecutor<PeminjamanBuku> {
    Optional<PeminjamanBuku> findByUser(User user);
}
