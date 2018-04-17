package com.kedacom.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.kedacom.demo.model.User;

public interface UserManageService {
    /**
     * �����û�
     * @param user
     * @return
     */
	public int createUser(User user);
	
	/**
	 * �����û���Ϣ
	 * @param user
	 */
	public void modifyUser(User user);
	
	/**
	 * �ļ�����
	 * @param response
	 * @param fileName
	 */
	public void downLoad(HttpServletResponse response, String fileName);
	
	/**
	 * ����id��ȡ�û�
	 * @param id
	 * @return
	 */
    public User getUserById(int id);
}
