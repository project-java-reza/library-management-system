package com.sinaukoding.librarymanagementsystem.repository.specification;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanBukuSpecification {

    public static Specification<PeminjamanBuku> withFilters(
        LocalDate tanggalPinjam,
        LocalDate tanggalKembali,
        String statusBukuPinjaman,
        String namaMahasiswa,
        String judulBuku
    ) {
        return (root, query, cb) -> {
            // Joins
            var buku = root.join("buku", JoinType.INNER);
            var user = root.join("user", JoinType.INNER);
            var mahasiswa = root.join("user").join("mahasiswa", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (tanggalPinjam != null) {
                predicates.add(cb.equal(root.get("tanggalPinjam"), tanggalPinjam));
            }

            if (tanggalKembali != null) {
                predicates.add(cb.equal(root.get("tanggalKembali"), tanggalKembali));
            }

            if (StringUtils.hasText(statusBukuPinjaman)) {
                predicates.add(cb.equal(
                    root.get("statusBukuPinjaman").as(String.class),
                    statusBukuPinjaman
                ));
            }

            if (StringUtils.hasText(namaMahasiswa)) {
                predicates.add(cb.like(
                    cb.lower(mahasiswa.get("nama")),
                    "%" + namaMahasiswa.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(judulBuku)) {
                predicates.add(cb.like(
                    cb.lower(buku.get("judulBuku")),
                    "%" + judulBuku.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
