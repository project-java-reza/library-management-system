package com.sinaukoding.librarymanagementsystem.entity.master;

import com.sinaukoding.librarymanagementsystem.entity.app.BaseEntity;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBuku;
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
@Table(name = "m_buku", indexes = {
        @Index(name = "idx_buku_created_date", columnList = "createdDate"),
        @Index(name = "idx_buku_modified_date", columnList = "modifiedDate"),
        @Index(name = "idx_buku_judul_buku", columnList = "judulBuku"),
        @Index(name = "idx_buku_penulis", columnList = "penulis"),
        @Index(name = "idx_buku_penerbit", columnList = "penerbit"),
        @Index(name = "idx_buku_tahun_terbit", columnList = "tahunTerbit"),
        @Index(name = "idx_buku_jumlah_salinan", columnList = "jumlahSalinan"),
        @Index(name = "idx_buku_lokasi_rak", columnList = "lokasiRak"),
        @Index(name = "idx_buku_status_buku", columnList = "statusBuku"),
})
public class Buku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "judul_buku", nullable = false)
    private String judulBuku;

    @Column(name = "penulis", nullable = false)
    private String penulis;

    @Column(name = "penerbit", nullable = false)
    private String penerbit;

    @Column(name = "tahun_terbit", nullable = false)
    private Integer tahunTerbit;

    @Column(name = "jumlah_salinan", nullable = false)
    private Integer jumlahSalinan;

    @Column(name = "lokasi_rak", nullable = false)
    private String lokasiRak;

    @ManyToOne
    @JoinColumn(name = "kategori_buku_id", nullable = false)
    private KategoriBuku kategoriBukuId;

    @OneToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "nama_kategori", nullable = false)
    private String namaKategori;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_buku", nullable = false)
    private StatusBuku statusBuku;
}
