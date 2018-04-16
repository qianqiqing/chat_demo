package com.kedacom.demo.wesocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.common.ChatServer;
import com.kedacom.demo.model.User;

public class ChatRoom extends AbstractWebSocketHandler{
	private Logger logger = Logger.getLogger(this.getClass());
		
	public final static List<WebSocketSession> sessionList = Collections.synchronizedList(new ArrayList<WebSocketSession>());
    private static int onlineCount = 0; //�����û���
    private String username;            //�û���

    private static List<String> list = new ArrayList();   //�����б�
    private static Map routetab = new HashMap();        //�û�����session�Ķ�Ӧmap
    
    FileOutputStream outPut;
    
    private final String loadFilePath = "D:\\loadFiles\\";
 
	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	 
	
	@Override    
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {  
		sessionList.add(webSocketSession);  
		addOnlineCount(); 
		User user = (User) webSocketSession.getAttributes().get("currentUser");    //��ȡ��ǰ�û�
        username = user.getName();
        list.add(username);
        routetab.put(username, webSocketSession);   //���û�����session�󶨵�·�ɱ�
        TextMessage message = getMessage("[" + username + "]����������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice",  list);
        broadcast(message);     //�㲥
    }    
	
	@Override    
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {    
        sessionList.remove(webSocketSession);  
        subOnlineCount();  
        list.remove(username);
        routetab.remove(username);
        TextMessage message = getMessage("[" + username +"]�뿪��������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice", list);
        broadcast(message);   
    }
	
	@Override  
    public void handleTextMessage(WebSocketSession websocketsession, TextMessage message) {  
        String payload=message.getPayload();  
        String textString; 
        JSONObject jsonObject = JSON.parseObject(payload);
        JSONObject messageObject = JSON.parseObject(jsonObject.get("message").toString());
        String type = jsonObject.get("type").toString();
        String content = messageObject.get("content").toString();
        try{
        	if("fileStart".equals(type)){                 //��ʼ�����ļ�
            	outPut=new FileOutputStream(new File(loadFilePath + content));   //�ڷ�����ƶ�·�������µ��ļ�
            }else if("fileFinish".equals(type)){           //�ļ��������
            	outPut.close();
            	TextMessage messageContent = getMessage(messageObject.get("from").toString() + " ˵ :", "message", null);
            	if(messageObject.get("to") == null || messageObject.get("to").equals("")){      //���toΪ�գ����͸�������;�����Ϊ��,���ָ�����û�������Ϣ
                	broadcast(messageContent);   
                	//����ͼƬ
                	for(WebSocketSession session : sessionList){
                		sendFile(content, session);
                	}
                }else{
                    String [] userlist = messageObject.get("to").toString().split(",");
                    for(String user : userlist){
                        if(!user.equals(messageObject.get("from"))){
                            sendMessage(messageContent, (WebSocketSession) routetab.get(user));     //�ֱ��͸�ÿ��ָ���û�
                            sendFile(content, (WebSocketSession) routetab.get(user));
                        }
                    }
                }
            }else{
            	if(messageObject.get("to") == null || messageObject.get("to").equals("")){      //���toΪ�գ����͸�������;�����Ϊ��,���ָ�����û�������Ϣ
                	broadcast(message);   
                }else{
                    String [] userlist = messageObject.get("to").toString().split(",");
                    sendMessage(message, (WebSocketSession) routetab.get(messageObject.get("from")));      //���͸��Լ�,���������
                    for(String user : userlist){
                        if(!user.equals(messageObject.get("from"))){
                        	sendMessage(message, (WebSocketSession) routetab.get(user));     //�ֱ��͸�ÿ��ָ���û�
                        }
                    }
                }
            }
        }catch(Exception e){
        	
        }
    }  
	
	@Override  
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message)  
    {  
        ByteBuffer buffer= message.getPayload();  
        try {  
            outPut.write(buffer.array());  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
	
	@Override    
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {    
        if(webSocketSession.isOpen()){    
            webSocketSession.close();    
        }    
    }
	
	@Override    
    public boolean supportsPartialMessages() {    
        return true;    
    }    
	
	public void sendMessage(TextMessage message, WebSocketSession session){
		try{
			session.sendMessage(message);
		}catch(Exception e){
			logger.error("������Ϣʧ��:"+e);
		}
	}
        
    public void sendFile(String fileName, WebSocketSession session){  
        FileInputStream input;  
        try {  
            File file=new File("D:\\loadFiles\\"+fileName);  
            input = new FileInputStream(file);  
            byte bytes[] = new byte[(int) file.length()];   
            input.read(bytes);  
            BinaryMessage byteMessage=new BinaryMessage(bytes);  
            session.sendMessage(byteMessage);  
            input.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     *  �㲥֪ͨ
     */
    public void broadcast(TextMessage _message){
    	try {
			for(WebSocketSession session: sessionList){
			    session.sendMessage(_message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * ��װ���ظ�ǰ̨����Ϣ
     */
    public TextMessage getMessage(String message, String type, List<String> list){
        JSONObject member = new JSONObject();
        member.put("message", message);
        member.put("type", type);
        member.put("list", list);
        TextMessage textMessage = new TextMessage(member.toString());
        return textMessage;
    }
    
    public  int getOnlineCount() {
        return onlineCount;
    }

    public  void addOnlineCount() {
        onlineCount++;
    }

    public  void subOnlineCount() {
        onlineCount--;
    }

}
