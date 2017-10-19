package com.wb.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {
	public static Field[] getAllFields(Class clazz){
		List<Field> fields = new ArrayList<Field>();
		if(clazz!=null){
			for(;clazz!=Object.class;clazz=clazz.getSuperclass()){
				Field[] f = clazz.getDeclaredFields();
				for(int i=0;i<f.length;i++){
					fields.add(f[i]);
				}
			}
		}
		
		Field[] fs = new Field[fields.size()];
		return fields.toArray(fs);
	}
}
