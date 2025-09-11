package com.sinaukoding.librarymanagementsystem.service.managementuser;


import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;

public interface AdminService {
    Admin create (AdminRequestRecord request);
}
