package com.sinaukoding.librarymanagementsystem.service.app;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.RegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;

public interface AuthService {

    SimpleMap login(LoginRequestRecord request);

    SimpleMap loginUser(LoginRequestRecord request);

    SimpleMap loginAdmin(LoginRequestRecord request);

    void register(RegisterRequestRecord request);

    void registerAdmin(AdminRegisterRequestRecord request);

    void logout(Admin userLoggedIn);

    void logout(User userLoggedIn);

}
