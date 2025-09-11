package com.sinaukoding.librarymanagementsystem.service.app;


import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;

public interface AuthService {

    SimpleMap login(LoginRequestRecord request);

    void logout(User userLoggedIn);

}
