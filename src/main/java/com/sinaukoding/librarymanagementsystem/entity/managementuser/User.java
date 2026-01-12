package com.sinaukoding.librarymanagementsystem.entity.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "m_user", indexes = {
        @Index(name = "idx_user_created_date", columnList = "createdDate"),
        @Index(name = "idx_user_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_status", columnList = "status"),
})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String nama;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Mahasiswa mahasiswa;

    private String token;
    private LocalDateTime expiredTokenAt;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "foto_upload_date")
    private LocalDateTime fotoUploadDate;

    @Column(name = "tanggal_bergabung")
    private LocalDate tanggalBergabung;

}
