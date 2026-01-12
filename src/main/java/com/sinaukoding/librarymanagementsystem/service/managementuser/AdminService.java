package com.sinaukoding.librarymanagementsystem.service.managementuser;


import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.ChangePasswordRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    Admin create (AdminRequestRecord request);

    Admin edit(AdminRequestRecord request);

    Admin editProfile(AdminProfileRequestRecord request);

    String uploadFoto(String id, MultipartFile file);

    void changePassword(ChangePasswordRequestRecord request);

    Page<SimpleMap> findAllProfileAdmin(AdminFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    SimpleMap findByUsername(String username);

    void deleteByIdAdmin(String id);
}
