package com.kedacom.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.kedacom.demo.model.User;

public interface UserManageService {
    /**
     * 创建用户
     * @param user
     * @return
     */
	public int createUser(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 */
	public void modifyUser(User user);
	
	/**
	 * 文件下载
	 * @param response
	 * @param fileName
	 */
	public void downLoad(HttpServletResponse response, String fileName);
	
	/**
	 * 根据id获取用户
	 * @param id
	 * @return
	 */
    public User getUserById(int id);
}
