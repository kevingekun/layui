package com.wb.jdbcutils.args;

public class ArgBind implements java.io.Serializable{
	private String sqlx;
	private Object[] args;
	
	public ArgBind(){}
	public ArgBind(String sqlx, Object[] args){
		this.sqlx = sqlx;
		this.args = args;
	}
	
	public String getSqlx() {
		return sqlx;
	}
	public void setSqlx(String sqlx) {
		this.sqlx = sqlx;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	
}
