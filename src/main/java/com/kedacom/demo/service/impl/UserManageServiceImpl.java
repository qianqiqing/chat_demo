package com.kedacom.demo.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.kedacom.demo.dao.UserDao;
import com.kedacom.demo.model.User;
import com.kedacom.demo.service.UserManageService;
import com.kedacom.demo.wesocket.ChatRoom;

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
	
	public void downLoad(HttpServletResponse response, String fileName){
		String path = ChatRoom.loadFilePath + fileName;
		String name = fileName.split("\\.")[0];
		try{
			File file = new File(path);
	        // 以流的形式下载文件。
	        InputStream fis = new BufferedInputStream(new FileInputStream(path));
	        byte[] buffer = new byte[fis.available()];
	        fis.read(buffer);
	        fis.close();
	        // 清空response
	        response.reset();
	        // 设置response的Header
	        response.setContentType("application/octet-stream;charset=utf-8");  
	        response.setHeader("Content-Disposition", "attachment;filename="  
	                + new String(fileName.getBytes(),"iso-8859-1"));  
	        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	        toClient.write(buffer);
	        toClient.flush();
	        toClient.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}

}
