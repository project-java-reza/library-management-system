package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void add(UserRequestRecord request);

    void edit(UserRequestRecord request);

    Page<SimpleMap> findAll(UserFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

}