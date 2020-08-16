package com.xiaoshu.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class StuVo extends Stu{
	private String mname;

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date sdbirth1;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date sdbirth2;
	
	private Integer num;
	
	
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getSdbirth1() {
		return sdbirth1;
	}

	public void setSdbirth1(Date sdbirth1) {
		this.sdbirth1 = sdbirth1;
	}

	public Date getSdbirth2() {
		return sdbirth2;
	}

	public void setSdbirth2(Date sdbirth2) {
		this.sdbirth2 = sdbirth2;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}
	
	

}
