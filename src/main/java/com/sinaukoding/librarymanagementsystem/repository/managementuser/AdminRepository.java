package com.sinaukoding.librarymanagementsystem.repository.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String>, JpaSpecificationExecutor<Admin>  {
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndIdNot(String email, String id);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndIdNot(String username, String id);
    Optional<Admin> findByUsername(String username);
}
