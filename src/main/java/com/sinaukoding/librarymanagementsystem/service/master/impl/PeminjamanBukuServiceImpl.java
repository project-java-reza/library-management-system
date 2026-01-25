package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.PeminjamanBukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreatePeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanUserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchRecentPeminjamanRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdatePeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.StatusBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.specification.PeminjamanBukuSpecification;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeminjamanBukuServiceImpl implements PeminjamanBukuService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PeminjamanBukuRepository peminjamanBukuRepository;
    private final BukuRepository bukuRepository;
    private final PeminjamanBukuMapper peminjamanBukuMapper;
    private final StatusBukuRepository statusBukuRepository;
    private final AdminRepository adminRepository;

    @Override
    public PeminjamanBuku addPeminjamanBuku(PeminjamanBukuRequestRecord request, String username) {
        return addPeminjamanBukuInternal(request, username);
    }

    @Override
    public PeminjamanBuku addPeminjamanBuku(CreatePeminjamanBukuRequestRecord request, String username) {
        // Convert CreatePeminjamanBukuRequestRecord to PeminjamanBukuRequestRecord
        PeminjamanBukuRequestRecord oldRequest = new PeminjamanBukuRequestRecord(
            request.bukuId(),
            request.tanggalPinjam(),
            request.tanggalKembali(),
            request.statusBukuPinjaman(),
            request.denda() != null ? request.denda() : 0L
        );
        return addPeminjamanBukuInternal(oldRequest, username);
    }

    private PeminjamanBuku addPeminjamanBukuInternal(PeminjamanBukuRequestRecord request, String username) {
        try {
            log.info("Creating peminjaman buku for bukuId: {}", request.bukuId());

            log.info("User {} attempting to borrow book", username);

            // Get user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new EntityNotFoundException("Pengguna dengan username '" + username + "' tidak ditemukan.");
                    });

            // Validate request
            validasiMandatory(request);

            // Get book
            Buku buku = bukuRepository.findById(request.bukuId())
                    .orElseThrow(() -> {
                        log.error("Book not found: {}", request.bukuId());
                        return new EntityNotFoundException("Data Buku dengan ID '" + request.bukuId() + "' tidak ditemukan.");
                    });

            log.info("Book found: {}, current stock: {}", buku.getJudulBuku(), buku.getJumlahSalinan());

            // Check if book is available
            if (buku.getJumlahSalinan() <= 0) {
                log.warn("Book {} is out of stock", buku.getJudulBuku());
                throw new IllegalStateException("Buku '" + buku.getJudulBuku() + "' tidak tersedia karena stok habis. Silakan pilih buku lain.");
            }

            // Check if user has already borrowed this book and not returned yet
            boolean hasAlreadyBorrowed = peminjamanBukuRepository.findByUser(user).stream()
                    .anyMatch(peminjaman ->
                        peminjaman.getBuku().getId().equals(buku.getId()) &&
                        (peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM ||
                         peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.PENDING ||
                         peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.DENDA)
                    );

            if (hasAlreadyBorrowed) {
                log.warn("User {} has already borrowed book {}", username, buku.getJudulBuku());
                throw new IllegalStateException("Anda sedang meminjam buku '" + buku.getJudulBuku() + "'. Harap kembalikan buku terlebih dahulu sebelum meminjam lagi.");
            }

            // Decrease book copies
            buku.setJumlahSalinan(buku.getJumlahSalinan() - 1);

            // Update book status if no copies left
            if (buku.getJumlahSalinan() == 0) {
                StatusBuku statusBuku = statusBukuRepository.findByStatusBuku(EStatusBuku.TIDAK_TERSEDIA)
                        .orElseThrow(() -> {
                            log.error("Status buku TIDAK_TERSEDIA not found in database");
                            return new EntityNotFoundException("Status buku TIDAK_TERSEDIA tidak ditemukan di database.");
                        });
                buku.setStatusBukuTersedia(statusBuku);
                log.info("Book {} is now out of stock", buku.getJudulBuku());
            }

            bukuRepository.save(buku);

            // Create peminjaman record
            PeminjamanBuku peminjamanBuku = peminjamanBukuMapper.requestToEntity(request);
            peminjamanBuku.setTanggalPinjam(request.tanggalPinjam());
            peminjamanBuku.setTanggalKembali(request.tanggalKembali());
            peminjamanBuku.setStatusBukuPinjaman(request.statusBukuPinjaman());

            peminjamanBuku.setBuku(buku);
            peminjamanBuku.setUser(user);

            PeminjamanBuku simpanPeminjamanBuku = peminjamanBukuRepository.save(peminjamanBuku);

            log.info("Peminjaman created successfully: id={}, book={}, user={}",
                simpanPeminjamanBuku.getId(), buku.getJudulBuku(), username);

            return simpanPeminjamanBuku;
        } catch (BadCredentialsException | EntityNotFoundException | IllegalStateException e) {
            // Re-throw known exceptions
            log.error("Error in addPeminjamanBuku: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Wrap unexpected exceptions
            log.error("Unexpected error in addPeminjamanBuku: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat memproses peminjaman buku. Silakan coba lagi atau hubungi admin.", e);
        }
    }

    @Override
    public Page<SimpleMap> findAllPeminjamanBuku(PeminjamanBukuFilterRecord filterRequest, Pageable pageable) {
        log.info("Finding all peminjaman buku with filters: {}", filterRequest);

        // Convert enum to string for specification
        String statusBukuPinjamanStr = filterRequest.statusBukuPinjaman() != null
            ? filterRequest.statusBukuPinjaman().name()
            : null;

        // Build specification
        Specification<PeminjamanBuku> spec = PeminjamanBukuSpecification.withFilters(
            filterRequest.tanggalPinjam(),
            filterRequest.tanggalKembali(),
            statusBukuPinjamanStr,
            filterRequest.namaMahasiswa(),
            filterRequest.judulBuku()
        );

        // Execute query with specification and pageable
        Page<PeminjamanBuku> listPeminjamanBuku = peminjamanBukuRepository.findAll(spec, pageable);

        log.info("Found {} peminjaman records", listPeminjamanBuku.getTotalElements());

        // Calculate Numbering Variables
        int recordsTotal = (int) listPeminjamanBuku.getTotalElements();
        int recordsBeforeCurrentPage = (int) pageable.getOffset();
        int numberOfElementsInPage = listPeminjamanBuku.getNumberOfElements();

        // Determine sort direction from pageable
        boolean isAscending = true;
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            isAscending = order.isAscending();
        }
        boolean isDescending = !isAscending;

        List<SimpleMap> listData = new java.util.ArrayList<>();
        int index = 0;

        for (PeminjamanBuku peminjaman : listPeminjamanBuku.getContent()) {
            SimpleMap data = new SimpleMap();

            // Dynamic numbering berdasarkan sort direction
            int nomor;
            if (isDescending) {
                // DESC: Nomor mundur dari akhir halaman (3, 2, 1 untuk 3 data di halaman)
                nomor = recordsBeforeCurrentPage + numberOfElementsInPage - index;
            } else {
                // ASC: Nomor maju dari awal halaman (1, 2, 3 untuk 3 data di halaman)
                nomor = recordsBeforeCurrentPage + index + 1;
            }

            // Get Judul Buku safely with fallback
            String judulBuku = null;
            try {
                if (peminjaman.getBuku() != null && peminjaman.getBuku().getJudulBuku() != null) {
                    judulBuku = peminjaman.getBuku().getJudulBuku();
                }
            } catch (Exception e) {
                log.warn("Error getting judulBuku for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get Nama Kategori safely with fallback
            String namaKategori = null;
            try {
                if (peminjaman.getBuku() != null && peminjaman.getBuku().getNamaKategori() != null) {
                    namaKategori = peminjaman.getBuku().getNamaKategori();
                }
            } catch (Exception e) {
                log.warn("Error getting namaKategori for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get Lokasi Rak safely with fallback
            String lokasiRak = null;
            try {
                if (peminjaman.getBuku() != null && peminjaman.getBuku().getLokasiRak() != null) {
                    lokasiRak = peminjaman.getBuku().getLokasiRak();
                }
            } catch (Exception e) {
                log.warn("Error getting lokasiRak for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get Nama Mahasiswa safely with fallback
            String namaMahasiswa = null;
            try {
                if (peminjaman.getUser() != null && peminjaman.getUser().getNama() != null) {
                    namaMahasiswa = peminjaman.getUser().getNama();
                }
            } catch (Exception e) {
                log.warn("Error getting nama for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get Username safely with fallback
            String username = null;
            try {
                if (peminjaman.getUser() != null && peminjaman.getUser().getUsername() != null) {
                    username = peminjaman.getUser().getUsername();
                }
            } catch (Exception e) {
                log.warn("Error getting username for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get NIM safely with fallback
            String nim = null;
            try {
                if (peminjaman.getUser() != null &&
                    peminjaman.getUser().getMahasiswa() != null &&
                    peminjaman.getUser().getMahasiswa().getNim() != null) {
                    nim = peminjaman.getUser().getMahasiswa().getNim();
                }
            } catch (Exception e) {
                log.warn("Error getting nim for peminjaman id: {}", peminjaman.getId(), e);
            }

            // Get Jurusan safely with fallback
            String jurusan = null;
            try {
                if (peminjaman.getUser() != null &&
                    peminjaman.getUser().getMahasiswa() != null &&
                    peminjaman.getUser().getMahasiswa().getJurusan() != null) {
                    jurusan = peminjaman.getUser().getMahasiswa().getJurusan();
                }
            } catch (Exception e) {
                log.warn("Error getting jurusan for peminjaman id: {}", peminjaman.getId(), e);
            }

            data.put("no", nomor);
            data.put("id", peminjaman.getId());
            data.put("judulBuku", judulBuku);
            data.put("namaKategori", namaKategori);
            data.put("lokasiRak", lokasiRak);
            data.put("nama", namaMahasiswa);
            data.put("username", username);
            data.put("nim", nim);
            data.put("jurusan", jurusan);
            data.put("tanggalPinjam", peminjaman.getTanggalPinjam());
            data.put("tanggalKembali", peminjaman.getTanggalKembali());
            data.put("statusBukuPinjaman", peminjaman.getStatusBukuPinjaman());
            data.put("denda", peminjaman.getDenda());
            data.put("tanggalTenggat", peminjaman.getTanggalTenggat());

            // Hitung total keterlambatan
            // Batas maksimal = tanggalPinjam + 7 hari
            // Total keterlambatan = tanggalKembali - (tanggalPinjam + 7 hari)
            LocalDate batasMaksimal = peminjaman.getTanggalPinjam().plusDays(7);
            long totalKeterlambatan = 0;
            if (peminjaman.getTanggalKembali().isAfter(batasMaksimal)) {
                totalKeterlambatan = java.time.temporal.ChronoUnit.DAYS.between(batasMaksimal, peminjaman.getTanggalKembali());
            }
            data.put("totalKeterlambatan", totalKeterlambatan);

            listData.add(data);
            index++;
        }

        return AppPage.create(listData, pageable, listPeminjamanBuku.getTotalElements());
    }

    @Override
    public Page<SimpleMap> findAllPeminjamanBuku(SearchPeminjamanBukuRequestRecord searchRequest) {
        log.info("Finding all peminjaman buku with search request: {}", searchRequest);

        // Map sort column from database name to entity property name
        String entitySortColumn = mapSortColumnForAdmin(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "modifiedDate");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        // Build filter from search parameter
        PeminjamanBukuFilterRecord filterRequest = new PeminjamanBukuFilterRecord(
            null, null, null, null, null, null, null
        );

        if (searchRequest.status() != null && !searchRequest.status().isEmpty()) {
            try {
                StatusBukuPinjaman statusEnum = StatusBukuPinjaman.valueOf(searchRequest.status().toUpperCase());
                filterRequest = new PeminjamanBukuFilterRecord(
                    null, null, null, null, statusEnum, null, null
                );
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
                log.warn("Invalid status value: {}", searchRequest.status());
            }
        }

        // Call the original method with the built filter and pageable
        return findAllPeminjamanBuku(filterRequest, pageable);
    }

    /**
     * Map sort column for admin from database column name (snake_case) to entity property name (camelCase)
     */
    private String mapSortColumnForAdmin(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "modifiedDate";
        }

        return switch (sortColumn) {
            case "tanggal_pinjam" -> "tanggalPinjam";
            case "tanggal_kembali" -> "tanggalKembali";
            case "judul_buku", "judulBuku", "buku" -> "buku.judulBuku";
            case "nama", "nama_mahasiswa", "namaMahasiswa" -> "user.mahasiswa.nama";
            case "status_buku_pinjaman", "statusBukuPinjaman", "status" -> "statusBukuPinjaman";
            case "modified_date", "modifiedDate" -> "modifiedDate";
            case "created_date", "createdDate" -> "createdDate";
            default -> "modifiedDate";
        };
    }

    @Override
    public SimpleMap findByIdPeminjamanMahasiswa(String id) {
        try {
            log.info("Finding peminjaman by id: {}", id);

            if (id == null || id.isBlank()) {
                log.error("Peminjaman ID is null or blank");
                throw new IllegalArgumentException("ID peminjaman tidak boleh kosong");
            }

            PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Peminjaman not found: {}", id);
                        return new EntityNotFoundException("Data peminjaman dengan ID '" + id + "' tidak ditemukan.");
                    });

            log.info("Peminjaman found: book={}, user={}",
                peminjamanBuku.getBuku().getJudulBuku(),
                peminjamanBuku.getUser().getUsername());

            SimpleMap data = new SimpleMap();
            data.put("id", peminjamanBuku.getId());
            data.put("judulBuku", peminjamanBuku.getBuku().getJudulBuku());
            data.put("penulis", peminjamanBuku.getBuku().getPenulis());
            data.put("penerbit", peminjamanBuku.getBuku().getPenerbit());
            data.put("tahunTerbit", peminjamanBuku.getBuku().getTahunTerbit());
            data.put("isbn", peminjamanBuku.getBuku().getIsbn());
            data.put("namaKategori", peminjamanBuku.getBuku().getNamaKategori());
            data.put("lokasiRak", peminjamanBuku.getBuku().getLokasiRak());
            data.put("jumlahSalinan", peminjamanBuku.getBuku().getJumlahSalinan());
            data.put("statusBukuTersedia", peminjamanBuku.getBuku().getStatusBukuTersedia() != null ?
                    peminjamanBuku.getBuku().getStatusBukuTersedia().getStatusBuku().getLabel() : null);
            data.put("userId", peminjamanBuku.getUser().getId());
            data.put("nama", peminjamanBuku.getUser().getNama());
            data.put("username", peminjamanBuku.getUser().getUsername());
            data.put("email", peminjamanBuku.getUser().getEmail());
            data.put("tanggalPinjam", peminjamanBuku.getTanggalPinjam());
            data.put("tanggalKembali", peminjamanBuku.getTanggalKembali());
            data.put("statusBukuPinjaman", peminjamanBuku.getStatusBukuPinjaman());
            data.put("denda", peminjamanBuku.getDenda());
            data.put("tanggalTenggat", peminjamanBuku.getTanggalTenggat());

            // Hitung total keterlambatan
            // Batas maksimal = tanggalPinjam + 7 hari
            // Total keterlambatan = tanggalKembali - (tanggalPinjam + 7 hari)
            LocalDate batasMaksimal = peminjamanBuku.getTanggalPinjam().plusDays(7);
            long totalKeterlambatan = 0;
            if (peminjamanBuku.getTanggalKembali().isAfter(batasMaksimal)) {
                totalKeterlambatan = java.time.temporal.ChronoUnit.DAYS.between(batasMaksimal, peminjamanBuku.getTanggalKembali());
            }
            data.put("totalKeterlambatan", totalKeterlambatan);

            return data;
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error in findByIdPeminjamanMahasiswa: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in findByIdPeminjamanMahasiswa: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data peminjaman. Silakan coba lagi.", e);
        }
    }

    @Override
    public void deleteByIdPeminjamanMahasiswaSelesai(String id) {
        try {
            log.info("Deleting peminjaman: id={}", id);

            if (id == null || id.isBlank()) {
                log.error("Peminjaman ID is null or blank");
                throw new IllegalArgumentException("ID peminjaman tidak boleh kosong");
            }

            PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Data peminjaman dengan ID '" + id + "' tidak ditemukan."));

            peminjamanBukuRepository.deleteById(id);

            log.info("Peminjaman deleted successfully: id={}", id);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error in deleteByIdPeminjamanMahasiswaSelesai: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in deleteByIdPeminjamanMahasiswaSelesai: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat menghapus data peminjaman. Silakan coba lagi.", e);
        }
    }

    @Override
    public SimpleMap updatePeminjamanBuku(String id, PeminjamanBukuRequestRecord request, String username) {
        return updatePeminjamanBukuInternal(id, request, username);
    }

    @Override
    public SimpleMap updatePeminjamanBuku(String id, UpdatePeminjamanBukuRequestRecord request, String username) {
        // Convert UpdatePeminjamanBukuRequestRecord to PeminjamanBukuRequestRecord
        PeminjamanBukuRequestRecord oldRequest = new PeminjamanBukuRequestRecord(
            request.bukuId(),
            request.tanggalPinjam(),
            request.tanggalKembali(),
            request.statusBukuPinjaman(),
            request.denda()
        );
        return updatePeminjamanBukuInternal(id, oldRequest, username);
    }

    private SimpleMap updatePeminjamanBukuInternal(String id, PeminjamanBukuRequestRecord request, String username) {
        try {
            log.info("Updating peminjaman: id={}, username={}", id, username);

            if (id == null || id.isBlank()) {
                log.error("Peminjaman ID is null or blank");
                throw new IllegalArgumentException("ID peminjaman tidak boleh kosong");
            }

            // Get admin who is performing the update
            Admin admin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Admin not found: {}", username);
                        return new EntityNotFoundException("Admin dengan username '" + username + "' tidak ditemukan.");
                    });

            PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Peminjaman not found: {}", id);
                        return new EntityNotFoundException("Data peminjaman dengan ID '" + id + "' tidak ditemukan.");
                    });

            // Simpan status lama untuk validasi perubahan
            StatusBukuPinjaman oldStatus = peminjamanBuku.getStatusBukuPinjaman();

            // Set admin who is updating the peminjaman
            peminjamanBuku.setAdmin(admin);
            log.info("Admin {} akan mengupdate peminjaman id {}", username, id);

            // Log nilai awal
            log.info("BEFORE UPDATE - statusBukuPinjaman: {}", peminjamanBuku.getStatusBukuPinjaman());
            log.info("REQUEST - statusBukuPinjaman: {}", request.statusBukuPinjaman());
            log.info("REQUEST - tanggalPinjam: {}", request.tanggalPinjam());
            log.info("REQUEST - tanggalKembali: {}", request.tanggalKembali());
            log.info("REQUEST - denda: {}", request.denda());

            // Validasi untuk update: minimal satu field yang di-update tidak null
            boolean hasUpdate = false;
            if (request.tanggalPinjam() != null) {
                peminjamanBuku.setTanggalPinjam(request.tanggalPinjam());
                hasUpdate = true;
                log.info("Updating tanggalPinjam to: {}", request.tanggalPinjam());
            }
            if (request.tanggalKembali() != null) {
                peminjamanBuku.setTanggalKembali(request.tanggalKembali());
                hasUpdate = true;
                log.info("Updating tanggalKembali to: {}", request.tanggalKembali());
            }
            if (request.statusBukuPinjaman() != null) {
                peminjamanBuku.setStatusBukuPinjaman(request.statusBukuPinjaman());
                hasUpdate = true;
                log.info("Updating statusBukuPinjaman from {} to: {}", oldStatus, request.statusBukuPinjaman());
            }
            if (request.denda() != null) {
                peminjamanBuku.setDenda(request.denda());
                hasUpdate = true;
                log.info("Updating denda to: {}", request.denda());
            }

            if (!hasUpdate) {
                log.warn("No fields to update for peminjaman id: {}", id);
                throw new IllegalArgumentException("Minimal satu field harus diisi (tanggalPinjam, tanggalKembali, atau statusBukuPinjaman)");
            }

            // Log nilai setelah update
            log.info("AFTER UPDATE - statusBukuPinjaman: {}", peminjamanBuku.getStatusBukuPinjaman());

            // Validasi tanggal kembali must be after tanggal pinjam (jika keduanya di-update)
            if (peminjamanBuku.getTanggalPinjam() != null && peminjamanBuku.getTanggalKembali() != null) {
                if (peminjamanBuku.getTanggalKembali().isBefore(peminjamanBuku.getTanggalPinjam())) {
                    log.error("Invalid dates: tanggal kembali ({}) is before tanggal pinjam ({})",
                        peminjamanBuku.getTanggalKembali(), peminjamanBuku.getTanggalPinjam());
                    throw new IllegalArgumentException("Tanggal Kembali tidak boleh sebelum Tanggal Pinjam");
                }
            }

            // Logika untuk tanggal tenggat
            // 1. Jika status berubah menjadi DENDA (dari status lain), set tanggal tenggat = hari ini + 7 hari
            if (request.statusBukuPinjaman() == StatusBukuPinjaman.DENDA &&
                oldStatus != StatusBukuPinjaman.DENDA) {
                LocalDate tanggalTenggat = LocalDate.now().plusDays(7);
                peminjamanBuku.setTanggalTenggat(tanggalTenggat);
                log.info("Status berubah menjadi DENDA, tanggal tenggat di-set ke: {}", tanggalTenggat);
            }
            // 2. Jika denda di-update, status DENDA, DAN status TIDAK berubah, tambahkan 7 hari ke tanggal tenggat
            else if (request.denda() != null &&
                     request.statusBukuPinjaman() == null && // Status tidak sedang diubah
                     peminjamanBuku.getStatusBukuPinjaman() == StatusBukuPinjaman.DENDA &&
                     peminjamanBuku.getTanggalTenggat() != null) {
                LocalDate tanggalTenggatBaru = peminjamanBuku.getTanggalTenggat().plusDays(7);
                peminjamanBuku.setTanggalTenggat(tanggalTenggatBaru);
                log.info("Denda di-update untuk status DENDA, tanggal tenggat diperbarui: {} -> {}",
                    peminjamanBuku.getTanggalTenggat(), tanggalTenggatBaru);
            }
            // 3. Jika status berubah dari DENDA ke status lain (misal SUDAH_DIKEMBALIKAN), reset tanggal tenggat
            else if (request.statusBukuPinjaman() != null &&
                     request.statusBukuPinjaman() != StatusBukuPinjaman.DENDA &&
                     oldStatus == StatusBukuPinjaman.DENDA) {
                peminjamanBuku.setTanggalTenggat(null);
                log.info("Status berubah dari DENDA ke {}, tanggal tenggat di-reset", request.statusBukuPinjaman());
            }

            PeminjamanBuku updatedPeminjaman = peminjamanBukuRepository.save(peminjamanBuku);

            log.info("Peminjaman updated successfully: id={}", id);

            SimpleMap result = new SimpleMap();
            result.put("id", updatedPeminjaman.getId());
            result.put("judulBuku", updatedPeminjaman.getBuku().getJudulBuku());
            result.put("namaKategori", updatedPeminjaman.getBuku().getNamaKategori());
            result.put("nama", updatedPeminjaman.getUser().getNama());
            result.put("tanggalPinjam", updatedPeminjaman.getTanggalPinjam());
            result.put("tanggalKembali", updatedPeminjaman.getTanggalKembali());
            result.put("statusBukuPinjaman", updatedPeminjaman.getStatusBukuPinjaman());
            result.put("denda", updatedPeminjaman.getDenda());
            result.put("tanggalTenggat", updatedPeminjaman.getTanggalTenggat());

            return result;
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error in updatePeminjamanBuku: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in updatePeminjamanBuku: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengupdate data peminjaman. Silakan coba lagi.", e);
        }
    }

    private void validasiMandatory(PeminjamanBukuRequestRecord request) {
        if (request.bukuId() == null || request.bukuId().isBlank()) {
            log.error("Buku ID is null or blank");
            throw new IllegalArgumentException("ID Buku tidak boleh kosong");
        }
        if (request.statusBukuPinjaman() == null) {
            log.error("Status Buku Pinjaman is null");
            throw new IllegalArgumentException("Status Buku Pinjaman tidak boleh kosong");
        }
        if (request.tanggalPinjam() == null) {
            log.error("Tanggal Pinjam is null");
            throw new IllegalArgumentException("Tanggal Pinjam tidak boleh kosong");
        }
        if (request.tanggalKembali() == null) {
            log.error("Tanggal Kembali is null");
            throw new IllegalArgumentException("Tanggal Kembali tidak boleh kosong");
        }
        // Validate tanggal kembali must be after tanggal pinjam
        if (request.tanggalKembali().isBefore(request.tanggalPinjam())) {
            log.error("Invalid dates: tanggal kembali ({}) is before tanggal pinjam ({})",
                request.tanggalKembali(), request.tanggalPinjam());
            throw new IllegalArgumentException("Tanggal Kembali tidak boleh sebelum atau sama dengan Tanggal Pinjam");
        }
    }

    @Override
    public Page<SimpleMap> findPeminjamanByUser(String username, PeminjamanBukuFilterRecord filterRequest, Pageable pageable) {
        try {
            log.info("Finding peminjaman for user: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new EntityNotFoundException("User dengan username '" + username + "' tidak ditemukan.");
                    });

            // Get peminjaman by user from repository
            Page<PeminjamanBuku> userPeminjamanPage = peminjamanBukuRepository.findByUser(user, pageable);

            log.info("Found {} peminjaman records for user {}", userPeminjamanPage.getTotalElements(), username);

            List<SimpleMap> listData = userPeminjamanPage.stream().map(buku -> {
                SimpleMap data = new SimpleMap();
                data.put("id", buku.getId());
                data.put("judulBuku", buku.getBuku().getJudulBuku());
                data.put("penulis", buku.getBuku().getPenulis());
                data.put("penerbit", buku.getBuku().getPenerbit());
                data.put("namaKategori", buku.getBuku().getNamaKategori());
                data.put("lokasiRak", buku.getBuku().getLokasiRak());
                data.put("tanggalPinjam", buku.getTanggalPinjam());
                data.put("tanggalKembali", buku.getTanggalKembali());
                data.put("statusBukuPinjaman", buku.getStatusBukuPinjaman());
                data.put("denda", buku.getDenda());
                data.put("tanggalTenggat", buku.getTanggalTenggat());

                // Hitung total keterlambatan
                LocalDate batasMaksimal = buku.getTanggalPinjam().plusDays(7);
                long totalKeterlambatan = 0;
                if (buku.getTanggalKembali().isAfter(batasMaksimal)) {
                    totalKeterlambatan = java.time.temporal.ChronoUnit.DAYS.between(batasMaksimal, buku.getTanggalKembali());
                }
                data.put("totalKeterlambatan", totalKeterlambatan);

                return data;
            }).toList();

            return AppPage.create(listData, pageable, userPeminjamanPage.getTotalElements());
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error in findPeminjamanByUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in findPeminjamanByUser: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data peminjaman user. Silakan coba lagi.", e);
        }
    }

    @Override
    public Page<SimpleMap> findPeminjamanByUser(String username, SearchPeminjamanUserRequestRecord searchRequest) {
        try {
            log.info("Finding peminjaman for user: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new EntityNotFoundException("User dengan username '" + username + "' tidak ditemukan.");
                    });

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

            // Get peminjaman by user from repository
            Page<PeminjamanBuku> userPeminjamanPage = peminjamanBukuRepository.findByUser(user, pageable);

            log.info("Found {} peminjaman records for user {}", userPeminjamanPage.getTotalElements(), username);

            List<SimpleMap> listData = userPeminjamanPage.stream().map(buku -> {
                SimpleMap data = new SimpleMap();
                data.put("id", buku.getId());
                data.put("judulBuku", buku.getBuku().getJudulBuku());
                data.put("penulis", buku.getBuku().getPenulis());
                data.put("penerbit", buku.getBuku().getPenerbit());
                data.put("namaKategori", buku.getBuku().getNamaKategori());
                data.put("lokasiRak", buku.getBuku().getLokasiRak());
                data.put("tanggalPinjam", buku.getTanggalPinjam());
                data.put("tanggalKembali", buku.getTanggalKembali());
                data.put("statusBukuPinjaman", buku.getStatusBukuPinjaman());
                data.put("denda", buku.getDenda());
                data.put("tanggalTenggat", buku.getTanggalTenggat());

                // Hitung total keterlambatan
                LocalDate batasMaksimal = buku.getTanggalPinjam().plusDays(7);
                long totalKeterlambatan = 0;
                if (buku.getTanggalKembali().isAfter(batasMaksimal)) {
                    totalKeterlambatan = java.time.temporal.ChronoUnit.DAYS.between(batasMaksimal, buku.getTanggalKembali());
                }
                data.put("totalKeterlambatan", totalKeterlambatan);

                return data;
            }).toList();

            return AppPage.create(listData, pageable, userPeminjamanPage.getTotalElements());
        } catch (BadCredentialsException | EntityNotFoundException e) {
            log.error("Error in findPeminjamanByUser: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in findPeminjamanByUser: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data peminjaman user. Silakan coba lagi.", e);
        }
    }

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

    @Override
    public void returnBuku(String peminjamanId) {
        try {
            log.info("Processing return buku for peminjamanId: {}", peminjamanId);

            if (peminjamanId == null || peminjamanId.isBlank()) {
                log.error("Peminjaman ID is null or blank");
                throw new IllegalArgumentException("ID peminjaman tidak boleh kosong");
            }

            // Find peminjaman record
            PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findById(peminjamanId)
                    .orElseThrow(() -> {
                        log.error("Peminjaman not found: {}", peminjamanId);
                        return new EntityNotFoundException("Data peminjaman dengan ID '" + peminjamanId + "' tidak ditemukan.");
                    });

            log.info("Peminjaman found: book={}, user={}, current status={}",
                peminjamanBuku.getBuku().getJudulBuku(),
                peminjamanBuku.getUser().getUsername(),
                peminjamanBuku.getStatusBukuPinjaman());

            // Check if already returned or pending
            if (peminjamanBuku.getStatusBukuPinjaman() == StatusBukuPinjaman.SUDAH_DIKEMBALIKAN) {
                log.warn("Attempt to return already returned book: {}", peminjamanBuku.getBuku().getJudulBuku());
                throw new IllegalStateException("Buku '" + peminjamanBuku.getBuku().getJudulBuku() + "' sudah dikembalikan sebelumnya.");
            }

            if (peminjamanBuku.getStatusBukuPinjaman() == StatusBukuPinjaman.PENDING) {
                log.warn("Attempt to return book that is already pending approval: {}", peminjamanBuku.getBuku().getJudulBuku());
                throw new IllegalStateException("Buku '" + peminjamanBuku.getBuku().getJudulBuku() + "' sedang menunggu persetujuan pengembalian dari admin.");
            }

            // Check if status is DIPINJAM or DENDA (both can be returned)
            if (peminjamanBuku.getStatusBukuPinjaman() != StatusBukuPinjaman.DIPINJAM &&
                peminjamanBuku.getStatusBukuPinjaman() != StatusBukuPinjaman.DENDA) {
                log.warn("Invalid peminjaman status for return: {}", peminjamanBuku.getStatusBukuPinjaman());
                throw new IllegalStateException("Status peminjaman buku tidak valid untuk dikembalikan. Status saat ini: " + peminjamanBuku.getStatusBukuPinjaman());
            }

            // Update status peminjaman to PENDING (menunggu approval admin)
            peminjamanBuku.setStatusBukuPinjaman(StatusBukuPinjaman.PENDING);
            peminjamanBukuRepository.save(peminjamanBuku);

            log.info("Book return requested, waiting for admin approval: book={}, user={}",
                peminjamanBuku.getBuku().getJudulBuku(), peminjamanBuku.getUser().getUsername());
        } catch (IllegalArgumentException | EntityNotFoundException | IllegalStateException e) {
            log.error("Error in returnBuku: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in returnBuku: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat memproses pengembalian buku. Silakan coba lagi.", e);
        }
    }

    @Override
    public void approveReturnBuku(String peminjamanId) {
        try {
            log.info("Processing approve return buku for peminjamanId: {}", peminjamanId);

            if (peminjamanId == null || peminjamanId.isBlank()) {
                log.error("Peminjaman ID is null or blank");
                throw new IllegalArgumentException("ID peminjaman tidak boleh kosong");
            }

            // Find peminjaman record
            PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findById(peminjamanId)
                    .orElseThrow(() -> {
                        log.error("Peminjaman not found: {}", peminjamanId);
                        return new EntityNotFoundException("Data peminjaman dengan ID '" + peminjamanId + "' tidak ditemukan.");
                    });

            log.info("Peminjaman found: book={}, user={}, current status={}",
                peminjamanBuku.getBuku().getJudulBuku(),
                peminjamanBuku.getUser().getUsername(),
                peminjamanBuku.getStatusBukuPinjaman());

            // Check if already returned
            if (peminjamanBuku.getStatusBukuPinjaman() == StatusBukuPinjaman.SUDAH_DIKEMBALIKAN) {
                log.warn("Attempt to approve already returned book: {}", peminjamanBuku.getBuku().getJudulBuku());
                throw new IllegalStateException("Buku '" + peminjamanBuku.getBuku().getJudulBuku() + "' sudah dikembalikan sebelumnya.");
            }

            // Check if status is PENDING
            if (peminjamanBuku.getStatusBukuPinjaman() != StatusBukuPinjaman.PENDING) {
                log.warn("Invalid peminjaman status for approve: {}", peminjamanBuku.getStatusBukuPinjaman());
                throw new IllegalStateException("Status peminjaman buku tidak valid untuk disetujui. Status harus PENDING, Status saat ini: " + peminjamanBuku.getStatusBukuPinjaman());
            }

            // Update status peminjaman to SUDAH_DIKEMBALIKAN
            peminjamanBuku.setStatusBukuPinjaman(StatusBukuPinjaman.SUDAH_DIKEMBALIKAN);
            peminjamanBukuRepository.save(peminjamanBuku);

            // Update book stock
            Buku buku = peminjamanBuku.getBuku();
            int previousStock = buku.getJumlahSalinan();
            buku.setJumlahSalinan(buku.getJumlahSalinan() + 1);

            log.info("Book stock updated: {} from {} to {}", buku.getJudulBuku(), previousStock, buku.getJumlahSalinan());

            // Update book status to TERSEDIA if stock > 0
            if (buku.getJumlahSalinan() > 0) {
                StatusBuku statusBuku = statusBukuRepository.findByStatusBuku(EStatusBuku.TERSEDIA)
                        .orElseThrow(() -> {
                            log.error("Status buku TERSEDIA not found in database");
                            return new EntityNotFoundException("Status buku TERSEDIA tidak ditemukan di database.");
                        });
                buku.setStatusBukuTersedia(statusBuku);
                log.info("Book {} status updated to TERSEDIA", buku.getJudulBuku());
            }

            bukuRepository.save(buku);

            log.info("Book return approved successfully: book={}, user={}",
                buku.getJudulBuku(), peminjamanBuku.getUser().getUsername());
        } catch (IllegalArgumentException | EntityNotFoundException | IllegalStateException e) {
            log.error("Error in approveReturnBuku: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in approveReturnBuku: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat memproses persetujuan pengembalian buku. Silakan coba lagi.", e);
        }
    }

    @Override
    public Page<SimpleMap> getRecentPeminjaman(SearchRecentPeminjamanRequestRecord searchRequest) {
        try {
            log.info("Getting recent peminjaman with search request: {}", searchRequest);

            // Map sort column from database name to entity property name
            String entitySortColumn = mapSortColumnForRecent(searchRequest.sortColumn());

            // Convert SearchRequestRecord to Pageable
            Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                    ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "DESC"),
                              entitySortColumn)
                    : Sort.by(Sort.Direction.DESC, "createdDate");

            Pageable pageable = org.springframework.data.domain.PageRequest.of(
                    searchRequest.pageNumber() - 1,
                    searchRequest.pageSize(),
                    sort
            );

            // Get all peminjaman with pagination
            Page<PeminjamanBuku> peminjamanPage = peminjamanBukuRepository.findAll(pageable);

            log.info("Found {} peminjaman records", peminjamanPage.getTotalElements());

            // Calculate Numbering Variables
            int recordsTotal = (int) peminjamanPage.getTotalElements();
            int recordsBeforeCurrentPage = (int) pageable.getOffset();
            int numberOfElementsInPage = peminjamanPage.getNumberOfElements();

            // Determine sort direction from pageable
            boolean isAscending = true;
            if (pageable.getSort().isSorted()) {
                Sort.Order order = pageable.getSort().iterator().next();
                isAscending = order.isAscending();
            }

            List<SimpleMap> listData = new ArrayList<>();
            int index = 0;

            for (PeminjamanBuku peminjaman : peminjamanPage.getContent()) {
                SimpleMap data = new SimpleMap();

                // Dynamic numbering berdasarkan sort direction
                int nomor;
                if (isAscending) {
                    // ASC: Nomor maju dari awal halaman
                    nomor = recordsBeforeCurrentPage + index + 1;
                } else {
                    // DESC: Nomor mundur dari akhir halaman
                    nomor = recordsBeforeCurrentPage + numberOfElementsInPage - index;
                }

                // Get data safely with fallback
                String nim = null;
                String namaMahasiswa = null;
                try {
                    if (peminjaman.getUser() != null &&
                        peminjaman.getUser().getMahasiswa() != null) {
                        nim = peminjaman.getUser().getMahasiswa().getNim();
                        namaMahasiswa = peminjaman.getUser().getMahasiswa().getNama();
                    }
                } catch (Exception e) {
                    log.warn("Error getting mahasiswa data for peminjaman id: {}", peminjaman.getId(), e);
                }

                String judulBuku = null;
                try {
                    if (peminjaman.getBuku() != null) {
                        judulBuku = peminjaman.getBuku().getJudulBuku();
                    }
                } catch (Exception e) {
                    log.warn("Error getting buku data for peminjaman id: {}", peminjaman.getId(), e);
                }

                data.put("no", nomor);
                data.put("id", peminjaman.getId());
                data.put("nim", nim);
                data.put("namaMahasiswa", namaMahasiswa);
                data.put("judulBuku", judulBuku);
                data.put("tanggalPinjam", peminjaman.getTanggalPinjam());
                data.put("tanggalKembali", peminjaman.getTanggalKembali());
                data.put("statusBukuPinjaman", peminjaman.getStatusBukuPinjaman());
                data.put("denda", peminjaman.getDenda());
                data.put("tanggalTenggat", peminjaman.getTanggalTenggat());

                // Hitung total keterlambatan (dari tanggal kembali sampai tanggal tenggat)
                Long totalKeterlambatan = null;
                if (peminjaman.getTanggalTenggat() != null && peminjaman.getTanggalKembali() != null) {
                    long daysBetween = ChronoUnit.DAYS.between(
                        peminjaman.getTanggalKembali(),
                        peminjaman.getTanggalTenggat()
                    );
                    totalKeterlambatan = daysBetween > 0 ? daysBetween : 0;
                }
                data.put("totalKeterlambatan", totalKeterlambatan);

                listData.add(data);
                index++;
            }

            return AppPage.create(listData, pageable, peminjamanPage.getTotalElements());
        } catch (Exception e) {
            log.error("Unexpected error in getRecentPeminjaman: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengambil data peminjaman terbaru. Silakan coba lagi.", e);
        }
    }

    /**
     * Map sort column for recent peminjaman from database column name (snake_case) to entity property name (camelCase)
     */
    private String mapSortColumnForRecent(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "createdDate";
        }

        return switch (sortColumn) {
            case "tanggal_pinjam" -> "tanggalPinjam";
            case "tanggal_kembali" -> "tanggalKembali";
            case "judul_buku", "judulBuku", "buku" -> "buku.judulBuku";
            case "nama", "nama_mahasiswa", "namaMahasiswa" -> "user.mahasiswa.nama";
            case "nim", "nim_mahasiswa" -> "user.mahasiswa.nim";
            case "status_buku_pinjaman", "statusBukuPinjaman", "status" -> "statusBukuPinjaman";
            case "modified_date", "modifiedDate" -> "modifiedDate";
            case "created_date", "createdDate" -> "createdDate";
            default -> "createdDate";
        };
    }

    @Override
    public SimpleMap cekPeminjamanTerlambat() {
        try {
            log.info("Cek peminjaman terlambat - memproses update tanggal tenggat untuk status DENDA");

            // Cari semua peminjaman dengan status DENDA
            List<PeminjamanBuku> peminjamanDendaList = peminjamanBukuRepository.findAll().stream()
                    .filter(peminjaman -> peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.DENDA)
                    .toList();

            log.info("Ditemukan {} peminjaman dengan status DENDA", peminjamanDendaList.size());

            int updatedCount = 0;
            List<SimpleMap> updatedPeminjamanList = new ArrayList<>();

            for (PeminjamanBuku peminjaman : peminjamanDendaList) {
                // Update tanggalTenggat = tanggalKembali + 7 hari
                LocalDate tanggalTenggatBaru = peminjaman.getTanggalKembali().plusDays(7);
                LocalDate tanggalTenggatLama = peminjaman.getTanggalTenggat();

                // tanggalPinjam TIDAK diubah, tetap tanggal awal peminjaman
                peminjaman.setTanggalTenggat(tanggalTenggatBaru);
                peminjamanBukuRepository.save(peminjaman);

                log.info("Peminjaman ID {} diupdate: tanggalTenggat {} -> {}, buku: {}, user: {}",
                    peminjaman.getId(),
                    tanggalTenggatLama,
                    tanggalTenggatBaru,
                    peminjaman.getBuku().getJudulBuku(),
                    peminjaman.getUser().getUsername());

                // Tambah ke list untuk response
                SimpleMap peminjamanData = new SimpleMap();
                peminjamanData.put("id", peminjaman.getId());
                peminjamanData.put("judulBuku", peminjaman.getBuku().getJudulBuku());
                peminjamanData.put("nama", peminjaman.getUser().getNama());
                peminjamanData.put("username", peminjaman.getUser().getUsername());
                peminjamanData.put("tanggalPinjam", peminjaman.getTanggalPinjam()); // Tidak berubah
                peminjamanData.put("tanggalTenggatLama", tanggalTenggatLama);
                peminjamanData.put("tanggalTenggatBaru", tanggalTenggatBaru);
                peminjamanData.put("tanggalKembali", peminjaman.getTanggalKembali());
                peminjamanData.put("denda", peminjaman.getDenda());
                updatedPeminjamanList.add(peminjamanData);

                updatedCount++;
            }

            SimpleMap result = new SimpleMap();
            result.put("totalDenda", peminjamanDendaList.size());
            result.put("totalUpdated", updatedCount);
            result.put("updatedPeminjaman", updatedPeminjamanList);
            result.put("message", String.format("Berhasil mengupdate tanggal tenggat untuk %d peminjaman dengan status DENDA", updatedCount));

            log.info("Selesai cek peminjaman terlambat: {} peminjaman diupdate", updatedCount);

            return result;
        } catch (Exception e) {
            log.error("Error dalam cekPeminjamanTerlambat: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat mengecek peminjaman terlambat. Silakan coba lagi.", e);
        }
    }
}
