package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.IdRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanUserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchRecentPeminjamanRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.dashboard.DashboardService;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/peminjaman")
public class PeminjamanBukuControllerUser {

    private final JwtAuthenticationConfig jwtAuthenticationConfig;
    private final PeminjamanBukuService peminjamanBukuService;
    private final JwtUtil jwtUtil;

    /**
     * Map sort column from database column name (snake_case) to entity property name (camelCase)
     */
    private String mapSortColumn(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "tanggalPinjam";
        }

        return switch (sortColumn) {
            case "tanggal_pinjam" -> "tanggalPinjam";
            case "tanggal_kembali" -> "tanggalKembali";
            case "judul_buku", "judulBuku", "buku" -> "buku.judulBuku";
            case "nama", "nama_mahasiswa", "namaMahasiswa" -> "user.mahasiswa.nama";
            case "status_buku_pinjaman", "statusBukuPinjaman", "status" -> "statusBukuPinjaman";
            case "modified_date", "modifiedDate" -> "modifiedDate";
            case "created_date", "createdDate" -> "createdDate";
            default -> "tanggalPinjam";
        };
    }

    @PostMapping
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@RequestBody PeminjamanBukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        peminjamanBukuService.addPeminjamanBuku(request, jwtToken);
        return BaseResponse.ok("Data Buku berhasil disimpan", null);
    }

    @PostMapping("/find-all")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getPeminjamanUser(HttpServletRequest httpServletRequest,
                                              @RequestBody SearchPeminjamanUserRequestRecord searchRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        String username = jwtUtil.extractUsername(jwtToken);

        // Map sort column from database name to entity property name
        String entitySortColumn = mapSortColumn(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "tanggalPinjam");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        return BaseResponse.ok("Berhasil mendapatkan data peminjaman", peminjamanBukuService.findPeminjamanByUser(username, null, pageable));
    }

    @PostMapping("/find-by-id")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getPeminjamanById(@RequestBody IdRequestRecord request) {
        return BaseResponse.ok(null, peminjamanBukuService.findByIdPeminjamanMahasiswa(request.id()));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> returnBuku(@PathVariable String id) {
        peminjamanBukuService.returnBuku(id);
        return BaseResponse.ok("Buku berhasil dikembalikan", null);
    }
}
