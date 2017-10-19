package com.wb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class RequestUtils{
	public static void printRequestParameters(HttpServletRequest request){
		Map map=request.getParameterMap();
	    Set keSet=map.entrySet();
	    for(Iterator itr=keSet.iterator();itr.hasNext();){
	        Map.Entry me=(Map.Entry)itr.next();
	        Object ok=me.getKey();
	        Object ov=me.getValue();
	        String[] value=new String[1];
	        if(ov instanceof String[]){
	            value=(String[])ov;
	        }else{
	            value[0]=ov.toString();
	        }

	        for(int k=0;k<value.length;k++){
	            System.out.println(ok+"="+value[k]);
	        }
	    }
	}
	
	public static Date getParameter(HttpServletRequest request, String name, String format, Date defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		Date date = defaultValue;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
		}catch(ParseException e){
			e.printStackTrace();
		}
		return date;
	}
	
	public static double getParameter(HttpServletRequest request,String name,double defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		return Double.parseDouble(value);
	}
	
	public static float getParameter(HttpServletRequest request,String name,float defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		return Float.parseFloat(value);
	}
	
	public static int getParameter(HttpServletRequest request,String name,int defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		return Integer.parseInt(value);
	}
	
	public static long getParameter(HttpServletRequest request,String name,long defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		return Long.parseLong(value);
	}
	
	public static String getParameter(HttpServletRequest request,String name,String defaultValue){
		if(request ==null)
			return defaultValue;
		String value = request.getParameter(name);
		
		if(!StringUtils.hasText(value)){
			return defaultValue;
		}
		return value;
	}

}

