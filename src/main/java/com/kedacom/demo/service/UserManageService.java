package com.kedacom.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.kedacom.demo.model.User;

public interface UserManageService {
    /**
    **�����û� 
    **/	
	public int createUser(User user);
	/**
    **��ѯ�����û�
    **/	
	public List<User> getOnlineUser();
	/**
    **��½�û�������Ϣ
    **/
	public User getUserDetail(String name, String password);
	/**
    **�����û���Ϣ
    **/	
	public void modifyUser(User user);
	/**
    **�ļ�����
    **/	
	public void downLoad(HttpServletResponse response, String fileName);

}
