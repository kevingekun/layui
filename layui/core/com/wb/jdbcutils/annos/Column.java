package com.wb.jdbcutils.annos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Column{
	public String name();
	public boolean id() default false;
	public int length() default 0;
	public boolean nullable() default true;
	public int precision() default 0;
	public int scale() default 0;
}

