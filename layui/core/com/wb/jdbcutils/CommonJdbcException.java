package com.wb.jdbcutils;

public class CommonJdbcException extends RuntimeException {
	public CommonJdbcException(){
		super();
	}
	public CommonJdbcException(String msg){
		super(msg);
	}
	public CommonJdbcException(Throwable cause){
		super(cause);
	}
	public CommonJdbcException(String msg, Throwable cause){
		super(msg,cause);
	}
}
