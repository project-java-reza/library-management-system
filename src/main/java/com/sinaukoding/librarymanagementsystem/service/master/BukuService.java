package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;

public interface BukuService {
    Buku addProfileMahasiswaUser(BukuRequestRecord request, String token);
}
