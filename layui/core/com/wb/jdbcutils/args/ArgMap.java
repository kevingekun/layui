package com.wb.jdbcutils.args;

public class ArgMap implements java.io.Serializable{
	private String key;
	private Object value;
	
	public ArgMap(){}
	public ArgMap(String key, Object value){
		this.key = key;
		this.value = value;
	}
	
	
	public String getKey() {
		return key;
	}
	public void setkey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
