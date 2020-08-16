package com.xiaoshu.service;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.entity.Student;

@Service // 让spring自动去扫描读取
public class ItemcatListener implements MessageListener{

	@Autowired
	StudentMapper studentMapper;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String maid = textMessage.getText();
			
			Student s = new Student();
			s.setSex("男");
			s.setSname("王老六");
			s.setHobby("篮球");
			s.setBirthday(new Date());
			
			s.setMaid(Integer.parseInt(maid));
			
			studentMapper.insert(s);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
