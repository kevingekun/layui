package com.wb.utils;

import java.io.UnsupportedEncodingException;

import sun.misc.*;

public class Base64Utils {
	// 加密
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	// 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		String str="eyJxaWQiOiI4Nzc0NDc3NzE2MDkyOTEyMTEwNSIsInR5cGUiOiIyIiwiYWFjMTQ3IjoiMzcwMjA0MjAxNjA5MDczNTZYIiwiYWFjMDAzIjoi5rWL6K+VIn0=";
		//String after_encode=getBase64(str);
		//System.out.println(after_encode);
		System.out.println(getFromBase64(str));
		
		String name=java.net.URLEncoder.encode("测试", "UTF-8");
		System.out.println(name);
		String enname =java.net.URLDecoder.decode("%E6%9D%8E%E9%A1%BA%E9%A1%BA", "UTF-8");
		System.out.println(enname);
		System.out.println(getBase64(enname));
		System.out.println(getFromBase64(getBase64(enname)));
	}
}