package com.kedacom.demo.dao;

import java.util.List;

import com.kedacom.demo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    User selectByPrimaryKey(Integer id);
    
    User selectByName(String name);

    int updateByPrimaryKey(User user);
    
    List<User> getAllUser();
    
    List<User> getOnlineUser();
}