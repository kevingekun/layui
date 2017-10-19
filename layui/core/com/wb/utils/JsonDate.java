package com.wb.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDate implements JsonValueProcessor{
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public DateFormat getDateFormat(){
		return this.dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat){
		this.dateFormat = dateFormat;
	}
	public Object processArrayValue(Object value, JsonConfig jsonConfig){
		return process(value, jsonConfig);
	}
	public Object processObjectValue(String key, Object value, JsonConfig jsonConfig){
		return process(value, jsonConfig);
	}
	private Object process(Object value, JsonConfig jsonConfig){
		Object dateValue = value;
		if((dateValue instanceof java.sql.Date)){
			dateValue = new java.util.Date(((java.sql.Date)dateValue).getTime());
		}
		if((dateValue instanceof java.util.Date)){
			return this.dateFormat.format(dateValue);
		}
		return dateValue;
	}
}
