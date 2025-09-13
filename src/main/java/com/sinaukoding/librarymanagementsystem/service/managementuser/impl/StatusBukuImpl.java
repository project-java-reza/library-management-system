package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.StatusBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.StatusBukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusBukuImpl implements StatusBukuService {

    private final StatusBukuRepository statusBukuRepository;

    @Override
    public StatusBuku getOrSave(EStatusBuku statusBuku) {
        return statusBukuRepository.findByStatusBuku(statusBuku).orElseGet(()-> statusBukuRepository.save(StatusBuku.builder()
                .statusBuku(statusBuku)
                .build()));
    }
}
