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

	public String validate(User user) {
		String msg = null;
		Map<String, User> userMap = new HashMap<String, User>();
		List<User> allUser = userDao.getAllUser();
		for(User elementUser : allUser){
			userMap.put(elementUser.getName(), elementUser);
		}
		
		if(!userMap.containsKey(user.getName())){
			msg = "用户不存在！";
		}else{
			User validateUser = userMap.get(user.getName());
			if(!user.getPassword().equals(validateUser.getPassword())){
				msg = "密码不正确！";
			}
		}
		
		return msg;
	}
	
	public User validateUser(String name, String password){
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(password)){
			User user = userDao.selectByName(name);
			if (user != null){
				if(password.equals(user.getPassword())){
					return user;
				}
			}
		}
		return null;
	}


}
