package com.sinaukoding.librarymanagementsystem.service.app;


import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;

public interface AuthService {

    SimpleMap login(LoginRequestRecord request);

    void registerAdmin(AdminRequestRecord request);

    void registerUser(UserRequestRecord request);

    void logout(User userLoggedIn);

}
