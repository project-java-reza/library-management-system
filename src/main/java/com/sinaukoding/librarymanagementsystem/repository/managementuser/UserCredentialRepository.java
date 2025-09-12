package com.sinaukoding.librarymanagementsystem.repository.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {

}
