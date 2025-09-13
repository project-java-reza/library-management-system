package com.sinaukoding.librarymanagementsystem.service.managementuser;


import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Admin create (AdminRequestRecord request);

    Admin edit(AdminRequestRecord request);

    Page<SimpleMap> findAllProfileAdmin(AdminFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    void deleteByIdAdmin(String id);
}
