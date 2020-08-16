package com.xiaoshu.service;

import java.util.List;

import javax.jms.Destination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.MajorMapper;
import com.xiaoshu.dao.StuMapper;
import com.xiaoshu.entity.Major;
import com.xiaoshu.entity.Stu;
import com.xiaoshu.entity.StuVo;

@Service
public class StuService {
	
	@Autowired
	private StuMapper stuMapper;
	
	@Autowired
	private MajorMapper majorMapper;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination qd;

	public void addM(Major major){
		majorMapper.addM(major);
		
		//发送专业信息
		jmsTemplate.convertAndSend(qd, JSONObject.toJSONString(major));
	}
	
	public List<StuVo> countStu(){
		return stuMapper.countStu();
	}
	
	public void delStu(Integer id){
		stuMapper.deleteByPrimaryKey(id);
	}
	
	public void addStu(Stu stu){
		stuMapper.insert(stu);
	}
	
	public void updateStu(Stu stu){
		stuMapper.updateByPrimaryKeySelective(stu);
	}
	
	public Stu findByName(String name){
		Stu param = new Stu();
		param.setSdName(name);
		return stuMapper.selectOne(param );
	}
	
	public List<Major> findM(){
		return majorMapper.selectAll();
	}
	
	public List<StuVo> findList(StuVo stuVo){
		return stuMapper.findList(stuVo);
	}
	
	public PageInfo<StuVo> findPage(StuVo stuVo,Integer pageNum,Integer pageSize,String ordername,String order){
		//分页
		PageHelper.startPage(pageNum, pageSize);
		List<StuVo> list = stuMapper.findList(stuVo);
		
		return new PageInfo<StuVo>(list);
	}

}
