package com.ricky.service;

import com.ricky.pojo.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO getUser(Long id);

    List<UserDTO> listUsers();

    String createUser(String name);
}