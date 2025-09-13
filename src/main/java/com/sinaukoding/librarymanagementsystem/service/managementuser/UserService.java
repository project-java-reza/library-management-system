package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User add(UserRequestRecord request);

    User edit(UserRequestRecord request);

    Page<SimpleMap> findAllProfileUser(UserFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdUser(String id);

    void deleteByIdUser(String id);

}