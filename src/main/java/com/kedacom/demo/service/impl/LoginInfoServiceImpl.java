package com.kedacom.demo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kedacom.demo.dao.UserDao;
import com.kedacom.demo.model.User;
import com.kedacom.demo.service.LoginInfoService;


@Service
public class LoginInfoServiceImpl implements LoginInfoService {
	private Logger logger = Logger.getLogger(LoginInfoServiceImpl.class);
	
	@Autowired
	private UserDao userDao;

	public User validateUser(String name, String password) {
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(password)) {
			User user = userDao.selectByName(name);
			if (user != null) {
				if (password.equals(user.getPassword())) {
					return user;
				}
			}
		}
		return null;
	}


}
