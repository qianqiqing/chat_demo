package demo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kedacom.demo.model.User;
import com.kedacom.demo.service.UserManageService;
import com.kedacom.demo.service.impl.UserManageServiceImpl;


public class UserManageTest {
    @Autowired
    private UserManageService userManageService;
    
    @Test
    public void createTest(){
    	UserManageService userManageService1 = new UserManageServiceImpl();
    	User user = new User();
    	user.setName("test");
    	user.setPassword("123456");
    	user.setRole(0);
    	user.setStatus(1);
    	int id = userManageService1.createUser(user);
    	System.out.println(id);
    }
}
