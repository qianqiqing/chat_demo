package com.kedacom.demo.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.model.User;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket" , configurator = HttpSessionConfigurator.class)
public class ChatServer {
	private Logger logger = Logger.getLogger(this.getClass());
	
    private static int onlineCount = 0; //�����û���
    private static CopyOnWriteArraySet<ChatServer> webSocketSet = new CopyOnWriteArraySet<ChatServer>();
    private Session session;            //��ĳ���ͻ��˵����ӻỰ
    private String username;            //�û���
    private HttpSession httpSession;    //request��session

    private static List<String> list = new ArrayList();   //�����б�
    private static Map routetab = new HashMap();        //�û�����session�Ķ�Ӧmap

    /**
     * ���ӽ����ɹ����õķ���
     * */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        webSocketSet.add(this); 
        addOnlineCount();           //��������1;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user = (User) httpSession.getAttribute("currentUser");    //��ȡ��ǰ�û�
        username = user.getName();
        list.add(username);           //���������û��б�
        routetab.put(username, session);   //���û�����session�󶨵�·�ɱ�
        String message = getMessage("[" + username + "]����������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice",  list);
        broadcast(message);     //�㲥
    }

    /**
     * ���ӹرյ��õķ���
     * */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);    //��set��ɾ��
        subOnlineCount();             //����������1
        list.remove(username);        //�������б��Ƴ�����û�
        routetab.remove(username);
        String message = getMessage("[" + username +"]�뿪��������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice", list);
        broadcast(message);         //�㲥
    }
    
    /**
     * ���տͻ��˵���Ϣ
     * @param <T>
     * */    
    @OnMessage
    public void receiveBigText(String _message, boolean last) {
        JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        if(message.get("to") == null || message.get("to").equals("")){      //���toΪ�գ����͸�������;�����Ϊ��,���ָ�����û�������Ϣ
        	multiSend(_message);
        }else{
            String [] userlist = message.get("to").toString().split(",");
            singleSend(_message, (Session) routetab.get(message.get("from")));      //���͸��Լ�,���������
            for(String user : userlist){
                if(!user.equals(message.get("from"))){
                    singleSend(_message, (Session) routetab.get(user));     //�ֱ��͸�ÿ��ָ���û�
                }
            }
        }
    }
    
    /**
     * ��������ʱ����
     */
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
    
    /**
     *  �㲥֪ͨ
     */
    public void broadcast(String _message){
    	try {
			for(ChatServer chatServer: webSocketSet){
			    chatServer.session.getBasicRemote().sendText(_message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * �������û�������Ϣ
     */
    public void multiSend(String _message){
    	JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
		if("image".equals(chat.get("type"))){
    		for(ChatServer chatServer: webSocketSet){
    			sendFile(message.get("content").toString(), chatServer.session);
    		}
    	}else{
    		broadcast(_message);
    	}
    }
    
    /**
     * �Ե����û�������Ϣ
     */
    public void singleSend(String _message, Session session){
    	JSONObject chat = JSON.parseObject(_message);
        JSONObject message = JSON.parseObject(chat.get("message").toString());
        try {
        	if("message".equals(chat.get("type"))){
        		session.getBasicRemote().sendText(_message);
        	}else if("image".equals(chat.get("type"))){
        		sendFile(message.get("content").toString(), session);
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendFile(String fileName, Session session){
    	FileInputStream input;  
        try {  
            File file=new File("C:\\Users\\qianqiqing\\Desktop\\" + fileName);  
            input = new FileInputStream(file);  
            byte bytes[] = new byte[(int) file.length()];   
            input.read(bytes); 
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
//            BinaryMessage byteMessage=new BinaryMessage(bytes);  
            session.getBasicRemote().sendBinary(buffer);
            input.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }      
    }
    
    /**
     * ��װ���ظ�ǰ̨����Ϣ
     */
    public String getMessage(String message, String type, List<String> list){
        JSONObject member = new JSONObject();
        member.put("message", message);
        member.put("type", type);
        member.put("list", list);
        return member.toString();
    }
    
    public  int getOnlineCount() {
        return onlineCount;
    }

    public  void addOnlineCount() {
        ChatServer.onlineCount++;
    }

    public  void subOnlineCount() {
        ChatServer.onlineCount--;
    }
    
}
