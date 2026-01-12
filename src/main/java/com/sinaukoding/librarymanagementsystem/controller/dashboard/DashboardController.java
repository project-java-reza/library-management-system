package com.sinaukoding.librarymanagementsystem.controller.dashboard;

import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.SearchRecentPeminjamanRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.dashboard.DashboardService;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final PeminjamanBukuService peminjamanBukuService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'ANGGOTA')")
    public BaseResponse<?> getDashboardStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return BaseResponse.ok("Berhasil mendapatkan statistik dashboard", stats);
    }

    @PostMapping("/peminjaman-terbaru")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANGGOTA')")
    public BaseResponse<?> getRecentPeminjaman(@RequestBody SearchRecentPeminjamanRequestRecord searchRequest) {
        Page<SimpleMap> result =
            peminjamanBukuService.getRecentPeminjaman(searchRequest);
        return BaseResponse.ok("Berhasil mendapatkan data peminjaman terbaru", result);
    }
}
