package com.kedacom.demo.service;

import com.kedacom.demo.model.User;

public interface LoginInfoService {
	/**
	 * ��֤��¼��Ϣ
	 * @param name
	 * @param password
	 * @return
	 */
	public User validateUser(String name, String password);

}
