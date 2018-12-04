package com.isport.bean;

public class sys_param_t {

	private String id;
	private String param_name;
	private String param_desc;
	private String param_value;
	private int expire_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParam_name() {
		return param_name;
	}
	public void setParam_name(String param_name) {
		this.param_name = param_name;
	}
	public String getParam_desc() {
		return param_desc;
	}
	public void setParam_desc(String param_desc) {
		this.param_desc = param_desc;
	}
	public String getParam_value() {
		return param_value;
	}
	public void setParam_value(String param_value) {
		this.param_value = param_value;
	}
	public int getExpire_time() {
		return expire_time;
	}
	public void setExpire_time(int expire_time) {
		this.expire_time = expire_time;
	}


}
