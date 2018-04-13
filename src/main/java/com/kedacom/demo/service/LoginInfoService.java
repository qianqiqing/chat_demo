package com.kedacom.demo.service;

import com.kedacom.demo.model.User;

public interface LoginInfoService {
	
	public String validate(User user);
	
	public User validateUser(String name, String password);

}
