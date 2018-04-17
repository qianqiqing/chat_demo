package com.kedacom.demo.common.wesocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kedacom.demo.model.User;

/**
 * �̳�AbstractWebSocketHandlerʵ��webSocketͨ��
 * @author Ǯ����
 *
 */
public class ChatRoom extends AbstractWebSocketHandler {
	private Logger logger = Logger.getLogger(this.getClass());
		
	public static final String loadFilePath = "D:\\loadFiles\\";
	public static List<Integer> onLineUserIds = new ArrayList();
	private final static List<WebSocketSession> sessionList = Collections.synchronizedList(new ArrayList<WebSocketSession>());
    private static List<String> list = new ArrayList();                        //�����б�
    private static Map<String,WebSocketSession> routetab = new HashMap();      //�û�����session�Ķ�Ӧmap
    private static int onlineCount = 0;                                        //�����û���
    private String username;                                                   //��ǰ�û���
    private User currentUser;
   
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private FileOutputStream outPut;
	 
    /**
     * ���ӽ����ɹ����õķ���,��д����AbstractWebSocketHandler�еķ���
     * @param WebSocketSession
     */
	@Override    
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {  
		sessionList.add(webSocketSession);  
		addOnlineCount(); 
		currentUser = (User) webSocketSession.getAttributes().get("currentUser"); //��ȡ��ǰ�û�
        username = currentUser.getName();
        list.add(username);
        onLineUserIds.add(currentUser.getId());
        routetab.put(username, webSocketSession);                               //���û�����session�󶨵�·�ɱ�
        TextMessage message = getMessage("[" + username + "]����������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice",  list);
        broadcast(message);                                                     //�㲥֪ͨ
    }    
	
	/**
     * ���ӹرյ��õķ���,��д����AbstractWebSocketHandler�еķ���
     * @param webSocketSession
     * @param status
     */
	@Override    
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {    
        sessionList.remove(webSocketSession);  
        subOnlineCount();  
        list.remove(username);
        onLineUserIds.remove(currentUser.getId());
        routetab.remove(username);
        TextMessage message = getMessage("[" + username +"]�뿪��������,��ǰ��������Ϊ"+getOnlineCount()+"λ", "notice", list);
        broadcast(message);   
    }
	
	/**
     * ����ͻ��˷��Ͷ˵���Ϣ,��д����AbstractWebSocketHandler�еķ���
     * @description �ͻ��˷��͵���һ����װ��json�ַ��������������ˣ������ˣ���Ϣ���͵�  
     * @param webSocketSession
     * @param message
     */
	@Override  
    public void handleTextMessage(WebSocketSession websocketsession, TextMessage message) {  
        String payload = message.getPayload();  
        JSONObject jsonObject = JSON.parseObject(payload);
        JSONObject messageObject = JSON.parseObject(jsonObject.get("message").toString());
        String type = jsonObject.get("type").toString();
        String content = messageObject.get("content").toString();
        TextMessage textMessage;
        String htmlMessage;
        try {
        	if ("fileStart".equals(type)) {                                       //��ʼ�����ļ�
        		File file = new File(loadFilePath);
        		if (!file.exists()) {
        			file.mkdirs();
        		}
            	outPut=new FileOutputStream(new File(loadFilePath + content));    //�ڷ�����ƶ�·�������µ��ļ�
            } else if ("fileFinish".equals(type)) {                               //�ļ��������
            	outPut.close();
            	if ("image".equals(messageObject.get("fileType").toString())) {   //�����ͼƬ�����ؿͻ��˵���  ������Ϣ+ͼƬ�����ƣ������η���
            		htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " ˵ :";
            		textMessage = getMessage(htmlMessage, "message", null);
            		
            		sendMessage(textMessage, messageObject);
            	} else {                                                           //�ļ�����һ�������ص��ļ�������
            		String html = "<a href=\"#\" onclick=\"downLoad('"+content+"')\">"+content+"</a></br>";
            		htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " �����ļ� : " + html;
            		textMessage = getMessage(htmlMessage, "message", null);
            		
            		sendMessage(textMessage, messageObject);
            	}
            	
            } else {
            	htmlMessage = messageObject.get("from").toString() + " ("+format.format(new Date())+")" + " ˵ : " + content +"</br>";
            	textMessage = getMessage(htmlMessage, "message", null);
            	
            	sendMessage(textMessage, messageObject);
            }
        } catch (Exception e) {
        	logger.error("handleTextMessage exception:"+e);
        }
    }  
	
	/**
     * ����ͻ��˷��͵�BinaryMessage,��д����AbstractWebSocketHandler�еķ���
     * @param session
     * @param message
     */
	@Override  
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {  
        ByteBuffer buffer = message.getPayload();  
        try {  
            outPut.write(buffer.array());  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
	
	/**
     * webSocket��Ϣ�����쳣����,��д����AbstractWebSocketHandler�еķ���
     * @param webSocketSession
     * @param throwable
     */
	@Override    
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {    
        if (webSocketSession.isOpen()) {    
            webSocketSession.close();    
        }    
    }
	
	/**
     * ֧�ֶַη���,��д����AbstractWebSocketHandler�еķ���
     * @return [boolean] [��Ϊtrue��ʾ֧�ֶַη���]
     */
	@Override    
    public boolean supportsPartialMessages() {    
        return true;    
    }  
	
	/**
     * ������Ϣ
     * @description �����ͼƬ�����ؿͻ��˵��� ��������Ϣ+ͼƬ�����ƣ������η��ͣ�������ļ��򷵻��ļ�������
     */
	private void sendMessage(TextMessage message, JSONObject messageObject) {
		String fileType = messageObject.get("fileType").toString();
		if (messageObject.get("to") == null || messageObject.get("to").equals("")) {      //���toΪ�գ����͸�������;�����Ϊ��,���ָ�����û�������Ϣ
        	broadcast(message);
        	if ("image".equals(fileType)) {                                               //�����ͼƬ�����뽫ͼƬ�Ķ��������ݴ��ؿͻ���չʾ
        		//����ͼƬ
            	for (WebSocketSession session : sessionList) {
            		sendFile(messageObject.get("content").toString(), session);
            	}
        	}
        } else {
            String [] userlist = messageObject.get("to").toString().split(",");
            sendText(message, (WebSocketSession) routetab.get(messageObject.get("from")));//���͸��Լ�,���������
            if ("image".equals(fileType)) { 
            	sendFile(messageObject.get("content").toString(), (WebSocketSession) routetab.get(messageObject.get("from")));
            }
            for (String user : userlist) {
                if (!user.equals(messageObject.get("from"))) {
                	sendText(message, (WebSocketSession) routetab.get(user));             //�ֱ��͸�ÿ��ָ���û�
                	if ("image".equals(fileType)) { 
                		sendFile(messageObject.get("content").toString(), (WebSocketSession) routetab.get(user));
                	}
                }
            }
        }
	}
	
	/**
     * �����ı���Ϣ
     * @param [message]
     * @param [session]
     */
	private void sendText(TextMessage message, WebSocketSession session) {
		try {
			session.sendMessage(message);
		} catch (Exception e) {
			logger.error("������Ϣʧ��:"+e);
		}
	}
       
	/**
     * �����ļ��Ķ�������Ϣ��
     * @param [fileName]
     * @param [session]
     */
    private void sendFile(String fileName, WebSocketSession session) {  
        FileInputStream input;  
        try {  
            File file = new File(loadFilePath + fileName);  
            input = new FileInputStream(file);  
            byte bytes[] = new byte[(int) file.length()];   
            input.read(bytes);  
            BinaryMessage byteMessage = new BinaryMessage(bytes);  
            session.sendMessage(byteMessage);  
            input.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * ������Ϣ��������
     * @param [_message]
     */
    private void broadcast(TextMessage _message) {
    	try {
			for (WebSocketSession session: sessionList) {
			    session.sendMessage(_message);
			}
		} catch (IOException e) {
			logger.error("broadcast error :" + e);
		}
    }
    
    /**
     * ������Ϣ��������
     * @param [_message]
     */
    private TextMessage getMessage(String message, String type, List<String> list) {
        JSONObject member = new JSONObject();
        member.put("message", message);
        member.put("type", type);
        member.put("list", list);
        TextMessage textMessage = new TextMessage(member.toString());
        return textMessage;
    }
    
    /**
     * ��ȡ�����û���
     * @return
     */
    private int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        onlineCount++;
    }

    private void subOnlineCount() {
        onlineCount--;
    }

}
