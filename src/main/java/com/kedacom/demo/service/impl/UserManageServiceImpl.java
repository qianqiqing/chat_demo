package com.kedacom.demo.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedacom.demo.dao.UserDao;
import com.kedacom.demo.model.User;
import com.kedacom.demo.service.UserManageService;

@Service
public class UserManageServiceImpl implements UserManageService {
	private Logger logger = Logger.getLogger(this.getClass());
    
	@Autowired
	private UserDao userDao; 
	
	public int createUser(User user) {
		int id = userDao.insert(user);
		return id;
	}

	public List<User> getOnlineUser() {
		List<User> onLineUser = userDao.getOnlineUser();
		return onLineUser;
	}

	public User getUserDetail(String name, String password) {
//		User user = userDao.selectByNameAndPassword(name, password);
//		return user;
		return null;
	}

	public void modifyUser(User user) {
		try{
			userDao.updateByPrimaryKey(user);
		}catch(Exception e){
			logger.error("modify user error:" + e);
		}
	}

}
