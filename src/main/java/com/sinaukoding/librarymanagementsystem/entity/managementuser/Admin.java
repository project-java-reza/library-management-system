package com.sinaukoding.librarymanagementsystem.entity.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_admin", indexes = {
        @Index(name = "idx_admin_created_date", columnList = "createdDate"),
        @Index(name = "idx_admin_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_admin_username", columnList = "username"),
        @Index(name = "idx_admin_email", columnList = "email"),
        @Index(name = "idx_admin_status", columnList = "status"),
})
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, message = "Password minimal 8 karakter")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
    private Role role;

    private String token;
    private LocalDateTime expiredTokenAt;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "foto_upload_date")
    private java.time.LocalDateTime fotoUploadDate;

}