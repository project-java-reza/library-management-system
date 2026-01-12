package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface BukuRepository extends JpaRepository<Buku, String>, JpaSpecificationExecutor<Buku> {
    Boolean existsByLokasiRak(String lokasiRak);
}
