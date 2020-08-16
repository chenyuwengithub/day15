package com.xiaoshu.dao;

import java.util.List;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Stu;
import com.xiaoshu.entity.StuVo;

public interface StuMapper extends BaseMapper<Stu> {
	
	public List<StuVo> findList(StuVo stuVo);
	
	public List<StuVo> countStu();
}