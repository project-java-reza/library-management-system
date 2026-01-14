package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.BukuSearchRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateBukuRequestRecord;
import org.springframework.data.domain.Page;


public interface BukuService {
    Buku addBukuBaru(CreateBukuRequestRecord request, String username);

    Buku editBuku(UpdateBukuRequestRecord request, String username);

    Page<SimpleMap> findAllBuku(BukuSearchRequestRecord request);

    SimpleMap findByIdBuku(String id);

    SimpleMap getStatusBuku(String id);

    void deleteByIdBuku(String id);

}
