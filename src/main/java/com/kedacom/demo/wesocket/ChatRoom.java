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
    private static int onlineCount = 0; //在线用户数
    private String username;            //用户名

    private static List<String> list = new ArrayList();   //在线列表
    private static Map routetab = new HashMap();        //用户名和session的对应map
    
    FileOutputStream outPut;
    
    private final String loadFilePath = "D:\\loadFiles\\";
 
	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	 
	
	@Override    
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {  
		sessionList.add(webSocketSession);  
		addOnlineCount(); 
		User user = (User) webSocketSession.getAttributes().get("currentUser");    //获取当前用户
        username = user.getName();
        list.add(username);
        routetab.put(username, webSocketSession);   //将用户名和session绑定到路由表
        TextMessage message = getMessage("[" + username + "]加入聊天室,当前在线人数为"+getOnlineCount()+"位", "notice",  list);
        broadcast(message);     //广播
    }    
	
	@Override    
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {    
        sessionList.remove(webSocketSession);  
        subOnlineCount();  
        list.remove(username);
        routetab.remove(username);
        TextMessage message = getMessage("[" + username +"]离开了聊天室,当前在线人数为"+getOnlineCount()+"位", "notice", list);
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
        	if("fileStart".equals(type)){                 //开始传输文件
            	outPut=new FileOutputStream(new File(loadFilePath + content));   //在服务端制定路径创建新的文件
            }else if("fileFinish".equals(type)){           //文件传输结束
            	outPut.close();
            	TextMessage messageContent = getMessage(messageObject.get("from").toString() + " 说 :", "message", null);
            	if(messageObject.get("to") == null || messageObject.get("to").equals("")){      //如果to为空，发送给所有人;如果不为空,则对指定的用户发送消息
                	broadcast(messageContent);   
                	//发送图片
                	for(WebSocketSession session : sessionList){
                		sendFile(content, session);
                	}
                }else{
                    String [] userlist = messageObject.get("to").toString().split(",");
                    for(String user : userlist){
                        if(!user.equals(messageObject.get("from"))){
                            sendMessage(messageContent, (WebSocketSession) routetab.get(user));     //分别发送给每个指定用户
                            sendFile(content, (WebSocketSession) routetab.get(user));
                        }
                    }
                }
            }else{
            	if(messageObject.get("to") == null || messageObject.get("to").equals("")){      //如果to为空，发送给所有人;如果不为空,则对指定的用户发送消息
                	broadcast(message);   
                }else{
                    String [] userlist = messageObject.get("to").toString().split(",");
                    sendMessage(message, (WebSocketSession) routetab.get(messageObject.get("from")));      //发送给自己,这个别忘了
                    for(String user : userlist){
                        if(!user.equals(messageObject.get("from"))){
                        	sendMessage(message, (WebSocketSession) routetab.get(user));     //分别发送给每个指定用户
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
			logger.error("发送消息失败:"+e);
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
     *  广播通知
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
     * 组装返回给前台的消息
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
