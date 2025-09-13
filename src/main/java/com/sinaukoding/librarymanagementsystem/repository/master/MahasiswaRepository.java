package com.sinaukoding.librarymanagementsystem.repository.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MahasiswaRepository extends JpaRepository<Mahasiswa, String>, JpaSpecificationExecutor<Mahasiswa> {

    Boolean existsByPhoneNumber(String phoneNumber);
    Optional<Mahasiswa> findByUser(User user);

}
