package com.sinaukoding.librarymanagementsystem.entity.master;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "m_mahasiswa", indexes = {
        @Index(name = "idx_mahasiswa_created_date", columnList = "createdDate"),
        @Index(name = "idx_mahasiswa_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_mahasiswa_nim", columnList = "nim"),
        @Index(name = "idx_mahasiswa_jurusan", columnList = "jurusan"),
        @Index(name = "idx_mahasiswa_alamat", columnList = "alamat"),
        @Index(name = "idx_mahasiswa_phone_number", columnList = "phoneNumber"),
})
public class Mahasiswa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    @Size(max = 20, message = "Max karakter 20")
    private String nim;

    @Size(max = 20, message = "Max karakter 20")
    @Column(nullable = false)
    private String jurusan;

    @Size(max = 100, message = "Max karakter 100")
    @Column(nullable = false)
    private String alamat;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String nama;
}
