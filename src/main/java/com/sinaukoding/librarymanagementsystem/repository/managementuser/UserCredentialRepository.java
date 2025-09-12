package com.sinaukoding.librarymanagementsystem.repository.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByUsername(String username);
}
