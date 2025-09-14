package com.sinaukoding.librarymanagementsystem.entity.master;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.model.enums.KategoriBukuEnum;
import jakarta.persistence.*;
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
@Table(name = "m_kategori_buku", indexes = {
        @Index(name = "idx_nama_kategori_buku", columnList = "nama_kategori_buku"),
        @Index(name = "idx_deskripsi_kategori", columnList = "deskripsi_kategori"),
})
public class KategoriBuku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "nama_kategori_buku", nullable = false, unique = true)
    private String namaKategoriBuku;

    @Column(name = "deskripsi_kategori", length = 255)
    private String deskripsiKategori;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

}
