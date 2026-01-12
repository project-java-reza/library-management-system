package com.sinaukoding.librarymanagementsystem.service.dashboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardStats();
    Page<SimpleMap> getRecentPeminjaman(Pageable pageable);
}
