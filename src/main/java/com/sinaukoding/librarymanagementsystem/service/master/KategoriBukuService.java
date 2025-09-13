package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;

public interface KategoriBukuService {
    KategoriBuku addKategoriBuku(KategoriBukuRequestRecord request, String token);
}
