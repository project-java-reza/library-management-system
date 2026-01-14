package com.sinaukoding.librarymanagementsystem.repository.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Boolean existsByEmail(String email);
    Boolean existsByEmailAndIdNot(String email, String id);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndIdNot(String username, String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.mahasiswa WHERE u.id = :id")
    Optional<User> findByIdWithMahasiswa(String id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.mahasiswa WHERE u.token = :token")
    Optional<User> findByTokenWithMahasiswa(String token);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.mahasiswa WHERE u.username = :username")
    Optional<User> findByUsernameWithMahasiswa(String username);

}
