package com.sinaukoding.librarymanagementsystem.entity.master;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "m_peminjaman", indexes = {
        @Index(name = "idx_peminjaman_buku_created_date", columnList = "createdDate"),
        @Index(name = "idx_peminjaman_buku_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_tanggal_pinjam_buku", columnList = "tanggal_pinjam"),
        @Index(name = "idx_tanggal_kembali_buku", columnList = "tanggal_kembali"),
})
public class PeminjamanBuku extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "buku_id", nullable = false)
    private Buku buku;

    @Column(name = "tanggal_pinjam", nullable = false)
    private LocalDate tanggalPinjam;

    @Column(name = "tanggal_kembali", nullable = false)
    private LocalDate tanggalKembali;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_buku_pinjaman", nullable = false)
    private StatusBukuPinjaman statusBukuPinjaman;

    @Column(name = "denda", nullable = false)
    private Long denda = 0L;

    @Column(name = "tanggal_tenggat")
    private LocalDate tanggalTenggat;
}
