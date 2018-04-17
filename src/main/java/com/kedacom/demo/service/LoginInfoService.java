package com.kedacom.demo.service;

import com.kedacom.demo.model.User;

public interface LoginInfoService {
	/**
	 * 验证登录信息
	 * @param name
	 * @param password
	 * @return
	 */
	public User validateUser(String name, String password);

}
