package com.kedacom.demo.dao;

import java.util.List;

import com.kedacom.demo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
	/**
	 * ����idɾ��
	 * @param id
	 * @return
	 */
    int deleteByPrimaryKey(Integer id);

    /**
     * ����
     * @param record
     * @return
     */
    int insert(User record);

    /**
     * ����id��ѯ
     * @param id
     * @return
     */
    User selectByPrimaryKey(Integer id);
    
    /**
     * �������Ʋ�ѯ
     * @param name
     * @return
     */
    User selectByName(String name);

    /**
     * ����
     * @param user
     * @return
     */
    int updateByPrimaryKey(User user);
    
    /**
     * �û��б�
     * @return
     */
    List<User> getAllUser();
    
    /**
     * �����û��б�
     * @return
     */
    List<User> getOnlineUser();
}