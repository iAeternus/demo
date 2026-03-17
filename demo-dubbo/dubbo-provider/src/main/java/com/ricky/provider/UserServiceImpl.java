package com.ricky.provider;

import com.ricky.pojo.UserDTO;
import com.ricky.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService(version = "1.0")
public class UserServiceImpl implements UserService {

    private final Map<Long, UserDTO> db = new HashMap<>();

    @Override
    public UserDTO getUser(Long id) {
        return db.get(id);
    }

    @Override
    public List<UserDTO> listUsers() {
        return new ArrayList<>(db.values());
    }

    @Override
    public String createUser(String name) {
        long id = System.currentTimeMillis();

        UserDTO user = new UserDTO();
        user.setId(id);
        user.setName(name);

        db.put(id, user);

        return "OK";
    }
}