package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;

public interface StatusBukuService {
    StatusBuku getOrSave(EStatusBuku statusBuku);
}
