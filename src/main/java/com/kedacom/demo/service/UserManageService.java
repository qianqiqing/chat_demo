package com.kedacom.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.kedacom.demo.model.User;

public interface UserManageService {
    /**
    **创建用户 
    **/	
	public int createUser(User user);
	/**
    **查询在线用户
    **/	
	public List<User> getOnlineUser();
	/**
    **登陆用户基本信息
    **/
	public User getUserDetail(String name, String password);
	/**
    **更新用户信息
    **/	
	public void modifyUser(User user);
	/**
    **文件下载
    **/	
	public void downLoad(HttpServletResponse response, String fileName);

}
