package com.xiaoshu.entity;

public class MajorVO {

	private String maname;
	private Integer ct;
	public String getManame() {
		return maname;
	}
	public void setManame(String maname) {
		this.maname = maname;
	}
	public Integer getCt() {
		return ct;
	}
	public void setCt(Integer ct) {
		this.ct = ct;
	}
	public MajorVO(String maname, Integer ct) {
		super();
		this.maname = maname;
		this.ct = ct;
	}
	public MajorVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "MajorVO [maname=" + maname + ", ct=" + ct + "]";
	}
	
	
	
	
}
