package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface KategoriBukuRepository extends JpaRepository<KategoriBuku, String>, JpaSpecificationExecutor<KategoriBuku> {
    Optional<KategoriBuku> findByAdmin(Admin admin);
    Boolean existsByNamaKategoriBuku(String namaKategoriBuku);
    Boolean existsByNamaKategoriBukuAndIdNot(String namaKategoriBuku, String id);
}
