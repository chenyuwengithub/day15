package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage)message;
		
		try {
			String json = msg.getText();
			System.out.println("<<<<<<<<<<<<<专业信息>>>>>>>>>>>>>>:"+json);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
