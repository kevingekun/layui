package com.wb.jdbcutils;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.wb.jdbcutils.annos.Callable;
import com.wb.jdbcutils.annos.Column;
import com.wb.jdbcutils.annos.In;
import com.wb.jdbcutils.annos.Out;
import com.wb.jdbcutils.annos.Sequence;
import com.wb.jdbcutils.annos.Table;
import com.wb.jdbcutils.args.ArgBind;
import com.wb.jdbcutils.args.ArgMap;


public class CommonJdbcCore {
	//是否是开发模式
	private boolean devMode;
	//是否输出SQL
	private boolean showSql;
	//SQL执行最大时间
	private int maxsqltime;
	//SQL返回的最大结果集
	private int maxsqlcount;
	
	private String databaseDriver;
	
	//日志输出
	private static Logger logger = Logger.getLogger(CommonJdbcCore.class);
	
	private DataSource dataSource;

	private static final Map<Class<?>,PropertyDescriptor[]> propertiesCache =new HashMap<Class<?>,PropertyDescriptor[]>();
	
	public CommonJdbcCore(){
		
	}
	
	public int call(Object obj){
		return callx__(obj);
	}
	public int call(String spName,Object obj){
		return callx__(spName,obj);
	}
	public int call(Connection con, Object obj){
		return callx__(con,obj);
	}
	
	public Object save(Object obj){
		return savex__(obj);
	}
	public void saveOrUpdate(Object obj){
		 saveOrUpdate_(obj);
	}
	public Object save(Connection con, Object obj){
		return savex__(con,obj);
	}
	public <T> void save(List<T> objs){
		if(objs==null||objs.size()==0)
			return;
		for(int i=0;i<objs.size();i++){
			savex__(objs.get(i));
		}
	}
	public <T> void save(Connection con, List<T> objs){
		if(objs==null||objs.size()==0)
			return;
		for(int i=0;i<objs.size();i++){
			savex__(con,objs.get(i));
		}
	}
	public void update(Object obj){
		updatex__(obj);
	}
	public void update(Connection con, Object obj){
		updatex__(con,obj);
	}
	public <T> void update(List<T> objs){
		if(objs==null||objs.size()==0)
			return;
		for(int i=0;i<objs.size();i++){
			updatex__(objs.get(i));
		}
	}
	public <T> void update(Connection con, List<T> objs){
		if(objs==null||objs.size()==0)
			return;
		for(int i=0;i<objs.size();i++){
			updatex__(con,objs.get(i));
		}
	}
	
	/**
	 *	根据绑定变量执行sql
	 **/
	public int execute(String sql, Object... args){
		return execute__(sql, args);
	}
	public int execute(Connection con, String sql, Object... args){
		return execute__(con,sql,args);
	}
	public int execute(String sql){
		return execute__(sql);
	}
	public int execute(Connection con, String sql){
		return execute__(con,sql);
	}
	public int executeProcedure(String name, Object... args){
		 Connection conn= null;
		 CallableStatement stmt=null;
		 int j=0;
		 String sql="{ call "+name;
		 String paras="(";
		 for(int i=0;i<args.length;i++){
			 paras+="?,";
		 }
		 paras=paras.substring(0, paras.length()-1)+")";
		 sql=sql+paras+" }";
		 
		 try {
			  conn=getConnection();
			  stmt=conn.prepareCall(sql); 
			 for(int i=0;i<args.length;i++){
				 stmt.setString(i+1,args[i].toString());
			 }
			 j=stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(!stmt.isClosed()){
					stmt.close();
					stmt=null;
				}
				if(!conn.isClosed()){
					conn.close();
					conn=null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return j;
	}
	/**
	 *	根据sql，模板类，绑定变量参数查询整个结果集
	 **/
	public <T> List query(String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return query(binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T> List query(String sql, Class<T> clazz, Object... arguments){
		return query__(sql,clazz,arguments);
	}
	public <T> List queryNT(String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryNT(binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T> List queryNT(String sql, Class<T> clazz, Object... arguments){
		return queryNT__(sql,clazz,arguments);
	}
	public <T> List query(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return query(con,binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T> List query(Connection con, String sql, Class<T> clazz, Object... arguments){
		return query__(con,sql,clazz,arguments);
	}
	/**
	 *	根据sql，模板类，绑定变量参数查询对象
	 **/
	public <T>T queryObject(String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryObject(binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T>T queryObject(String sql, Class<T> clazz, Object... arguments){
		List list = query__(sql,clazz,arguments);
		if(list==null||list.isEmpty())
			return null;
		return (T)(list.get(0));
	}
	public <T>T queryObjectNT(String sql, Class<T> clazz, Object... arguments){
		List list = queryNT__(sql,clazz,arguments);
		if(list==null||list.isEmpty())
			return null;
		return (T)(list.get(0));
	}
	public <T>T queryObject(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryObject(con,binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T>T queryObject(Connection con, String sql, Class<T> clazz, Object... arguments){
		List list = query__(con,sql,clazz,arguments);
		if(list==null||list.isEmpty())
			return null;
		return (T)(list.get(0));
	}
	/**
	 *	根据sql，模板类，绑定变量参数查询整个结果集中的第一条记录
	 **/
	public <T>T queryFirst(String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryFirst(binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T>T queryFirstNT(String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryFirstNT(binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T>T queryFirst(String sql, Class<T> clazz, Object... arguments){
		if(sql==null)
			return null;
		sql=sql.toUpperCase();
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sql += " limit 1";
		}else if(sql.indexOf(".NEXTVAL")<0){
			sql="select * from ("+sql+") where rownum=1";
		}
		List resultList = query__(sql,clazz,arguments);
		if(resultList==null||resultList.size()==0)
			return null;
		return (T)resultList.get(0);
	}
	public <T>T queryFirstNT(String sql, Class<T> clazz, Object... arguments){
		if(sql==null)
			return null;
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sql += " limit 1";
		}else{
			if(sql.indexOf("where")>0||sql.indexOf("WHERE")>0)
				sql += " and rownum=1";
			else
				sql += " where rownum=1";
		}
		List resultList = queryNT__(sql,clazz,arguments);
		if(resultList==null||resultList.size()==0)
			return null;
		return (T)resultList.get(0);
	}
	public <T>T queryFirst(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return null;
		return queryFirst(con,binds.getSqlx(),clazz,binds.getArgs());
	}
	public <T>T queryFirst(Connection con, String sql, Class<T> clazz, Object... arguments){
		if(sql==null)
			return null;
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sql += " limit 1";
		}else{
			if(sql.indexOf("where")>0||sql.indexOf("WHERE")>0)
				sql += " and rownum=1";
			else
				sql += " where rownum=1";
		}
		List resultList = query__(con,sql,clazz,arguments);
		if(resultList==null||resultList.size()==0)
			return null;
		return (T)resultList.get(0);
	}
	/**
	 *	根据sql，模板类，绑定变量参数按分页对象查询整个结果集
	 *  查询结果集存在于分页对象中
	 **/
	public void queryPage(Page page,String sql, Class clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return;
		queryPage(page,binds.getSqlx(),clazz,binds.getArgs());
	}
	public void queryPage(Page page,String sql, Class clazz, Object... arguments){
		String sqlPage = processSqlPage(sql);
		String sqlCount = "select count(1) from ("+sql+")";
		Object[] args = new Object[arguments.length+2];
		for(int i=0;i<arguments.length;i++){
			args[i] = arguments[i];
		}
		
		
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sqlCount += " __inner__ ";
			args[arguments.length] = page.getPagesize()*(page.getPageno()-1);
			args[arguments.length+1] = page.getPagesize();
		}else{
			args[arguments.length] = (page.getPageno()-1)*page.getPagesize()+1;
			args[arguments.length+1] = page.getPagesize();
		}
		
		List results = query__(sqlPage,clazz,args);
		Object c = queryObject(sqlCount,Long.class,arguments);
		long count = c==null?0:(Long)c;
		page.setResult(results);
		page.setTotalrows((int)count);
		if(page.getPagesize()>0)
			if(count%page.getPagesize()==0)
				page.setPagetotalcount((int)count/page.getPagesize());
			else
				page.setPagetotalcount((int)count/page.getPagesize()+1);
	}
	public void queryPageNT(Page page,String sql, Class clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return;
		queryPageNT(page,binds.getSqlx(),clazz,binds.getArgs());
	}
	public void queryPageNT(Page page,String sql, Class clazz, Object... arguments){
		String sqlPage = processSqlPage(sql);
		String sqlCount = "select count(1) from ("+sql+")";
		Object[] args = new Object[arguments.length+2];
		for(int i=0;i<arguments.length;i++){
			args[i] = arguments[i];
		}
		
		
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sqlCount += " __inner__ ";
			args[arguments.length] = page.getPagesize()*(page.getPageno()-1);
			args[arguments.length+1] = page.getPagesize();
		}else{
			args[arguments.length] = (page.getPageno()-1)*page.getPagesize()+1;
			args[arguments.length+1] = page.getPagesize();
		}
		
		List results = queryNT__(sqlPage,clazz,args);
		Object c = queryObjectNT(sqlCount,Long.class,arguments);
		long count = c==null?0:(Long)c;
		page.setResult(results);
		page.setTotalrows((int)count);
		if(page.getPagesize()>0)
			if(count%page.getPagesize()==0)
				page.setPagetotalcount((int)count/page.getPagesize());
			else
				page.setPagetotalcount((int)count/page.getPagesize()+1);
	}
	public void queryPage(Connection con, Page page,String sql, Class clazz, List<ArgMap> arguments){
		ArgBind binds = processSqlBindArg(sql,arguments);
		if(binds==null)
			return;
		queryPage(con,page,binds.getSqlx(),clazz,binds.getArgs());
	}
	public void queryPage(Connection con, Page page,String sql, Class clazz, Object... arguments){
		String sqlPage = processSqlPage(sql);
		String sqlCount = "select count(1) from ("+sql+")";
		Object[] args = new Object[arguments.length+2];
		for(int i=0;i<arguments.length;i++){
			args[i] = arguments[i];
		}
		
		
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			sqlCount += " __inner__ ";
			args[arguments.length] = page.getPagesize()*(page.getPageno()-1);
			args[arguments.length+1] = page.getPagesize();
		}else{
			args[arguments.length] = (page.getPageno()-1)*page.getPagesize()+1;
			args[arguments.length+1] = page.getPagesize();
		}
		
		List results = query__(con,sqlPage,clazz,args);
		Object c = queryObject(sqlCount,Long.class,arguments);
		long count = c==null?0:(Long)c;
		page.setResult(results);
		page.setTotalrows((int)count);
		if(count%page.getPagesize()==0)
			page.setPagetotalcount((int)count/page.getPagesize());
		else
			page.setPagetotalcount((int)count/page.getPagesize()+1);
	}
	
	
	/**
	 *
	 *原始执行方法
	 *保存对象
	 *返回保存后的对象的ID，如果
	 **/
	private Object savex__(Object obj){
		Connection con = null;
		con = getConnection();
		try {
			return save__(con,obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e){
			e.printStackTrace();
		}finally{
			releaseConnection(con);
		}
		return null;
	}
	public Object savex__(Connection con, Object obj){
		try {
			return save__(con,obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e){
			e.printStackTrace();
		}
		return null;
	}
	private Object save__(Connection con, Object obj)throws NoSuchMethodException,InstantiationException,IllegalAccessException,InvocationTargetException,SecurityException{
		if(con==null)
			return null;
		
		Object returnValue = null;
		Class c = obj.getClass();
		StringBuffer sqlColumn = new StringBuffer();
		StringBuffer sqlValue = new StringBuffer();
		//获取表名
		Table annoTable = (Table)c.getAnnotation(Table.class);
		String tableName = annoTable.name();
		if(annoTable==null||tableName==null||tableName.length()==0){
			throw new CommonJdbcException("对象未指定数据库映射名(@Table(name=?))");
		}
		
		sqlColumn.append("insert into ").append(tableName).append("(");
		sqlValue.append(" values(");
		
		//******
		//mysql获取自增序列
		Method setIncrementMethod = null;
		
		//存放绑定变量的列表
		List args = new ArrayList();
		List<Object> argTypes = new ArrayList<Object>();
		//原对象的方法列表
		Method[] ms = c.getMethods();
		
		int idFlag = 0;
		int nmFlag = 0;
		for(int i=0;i<ms.length;i++){
			String methodName = ms[i].getName();
			if(methodName.startsWith("get")){
				Column annoColumn = ms[i].getAnnotation(Column.class);
				if(annoColumn!=null&&annoColumn.name()!=null&&annoColumn.name().length()>0){
					nmFlag++;
					if(nmFlag==1){
						sqlColumn.append(annoColumn.name());
						sqlValue.append("?");
					}else{
						sqlColumn.append(",").append(annoColumn.name());
						sqlValue.append(",?");
					}
					
					if(annoColumn.id()){
						//如果此属性设置了Id属性，则进行sequence查询
						idFlag++;
						if(idFlag>1)
							throw new CommonJdbcException("存在多个有效的id主键定义(@Column(id=true))");
						Sequence annoSeq = ms[i].getAnnotation(Sequence.class);
						if(annoSeq!=null&&annoSeq.name()!=null&&annoSeq.name().toUpperCase().equals("SEQUENCE")
								&&annoSeq.sequencename()!=null&&annoSeq.sequencename().length()>0){
							Long seqValue = queryObject(con,"SELECT "+annoSeq.sequencename()+".nextval from dual", Long.class);
							args.add(seqValue);
							returnValue = seqValue;
							//将主键id返回到对象中
							String setMethodName = "set"+methodName.substring(3,methodName.length());
							Method setMethod = c.getMethod(setMethodName,ms[i].getReturnType());
							setMethod.invoke(obj, seqValue);
						}else if(databaseDriver.toUpperCase().equals("MYSQL")&&annoSeq!=null&&annoSeq.name()!=null
								&&annoSeq.name().toUpperCase().equals("INCREMENT")){
							String setMethodName = "set"+methodName.substring(3,methodName.length());
							setIncrementMethod = c.getMethod(setMethodName,ms[i].getReturnType());
							args.add(null);
						}else{
							Object o = ms[i].invoke(obj);
							if(!databaseDriver.toUpperCase().equals("MYSQL")&&o==null){
								throw new CommonJdbcException("主键"+annoColumn.name()+"不能为空");
							}
							returnValue = o;
							processAnnoColumn(args,annoColumn,o);
						}
						argTypes.add(ms[i].getReturnType());
					}else{
						Object o = ms[i].invoke(obj);
						//如果设置了非空则判断值是否为空
						if(!annoColumn.nullable()){
							if(o==null){
								throw new CommonJdbcException("非空字段"+annoColumn.name()+"的实际值不能为空");
							}
						}
						processAnnoColumn(args,annoColumn,o);
						argTypes.add(ms[i].getReturnType());
					}
				}
			}
		}
		
		if(idFlag==0)
			returnValue = 0L;
		
		if(args.size()==0)
			throw new CommonJdbcException("未发现有效的数据库映射字段名(@Column(name=?))");
		if(args.size()!=argTypes.size())
			throw new CommonJdbcException("数据格式与类型不匹配");
		
		sqlValue.append(")");
		sqlColumn.append(")");
		
		sqlColumn.append(sqlValue);
		Object[] arguments = new Object[args.size()];
		Object[] argumentTypes = new Object[argTypes.size()];
		for(int i=0;i<args.size();i++){
			arguments[i] = args.get(i);
			argumentTypes[i] = argTypes.get(i);
		}
		
		PreparedStatement ps = null;
		try{
			String sqlx = sqlColumn.toString().toUpperCase();
			if(showSql)
				logger.info("sql="+sqlx);
			ps = con.prepareStatement(sqlx);
			processSql3(ps,argumentTypes,arguments);
			int count = ps.executeUpdate();
			
			//mysql寻找自增字段
			if(databaseDriver.toUpperCase().equals("MYSQL")&&setIncrementMethod!=null){
				Long seqValue = queryObject(con,"SELECT LAST_INSERT_ID() ", Long.class);
				setIncrementMethod.invoke(obj, seqValue);
			}
			
			if(count>0)
				return returnValue;
		}catch(SQLException e){
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		}finally{
			releasePS(ps);
		}
		return null;
	}
	
	private void updatex__(Object obj){
		Connection con = null;
		try {
			con = getConnection();
			update__(con,obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}finally{
			releaseConnection(con);
		}
	}
	private void saveOrUpdate_(Object obj){
		Connection con = null;
		try {
			con = getConnection();
			saveOrUpdate__(con,obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch(NoSuchMethodException e){
			e.printStackTrace();
		}finally{
			releaseConnection(con);
		}
	}
	private void updatex__(Connection con, Object obj){
		try {
			update__(con,obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void update__(Connection con, Object obj)throws InstantiationException,IllegalAccessException,InvocationTargetException,SecurityException{
		if(con==null)
			return;
		
		Class c = obj.getClass();
		StringBuffer sqlColumn = new StringBuffer();
		//获取表名
		Table annoTable = (Table)c.getAnnotation(Table.class);
		String tableName = annoTable.name();
		if(annoTable==null||tableName==null||tableName.length()==0){
			throw new CommonJdbcException("对象未指定数据库映射名(@Table(name=?))");
		}
		
		sqlColumn.append("update ").append(tableName).append(" set ");
		
		//存放绑定变量的列表
		List args = new ArrayList();
		List<Object> argTypes = new ArrayList<Object>();
		//原对象的方法列表
		Method[] ms = c.getMethods();
		
		int idFlag = 0;
		int nmFlag = 0;
		String whereSql = "";
		Object idValue = null;
		Object returnIdValue = null;
		for(int i=0;i<ms.length;i++){
			String methodName = ms[i].getName();
			if(methodName.startsWith("get")){
				Column annoColumn = ms[i].getAnnotation(Column.class);
				if(annoColumn!=null&&annoColumn.name()!=null&&annoColumn.name().length()>0){
					if(!annoColumn.id()){
						nmFlag++;
						if(nmFlag==1){
							sqlColumn.append(annoColumn.name()).append("=?");
						}else{
							sqlColumn.append(",").append(annoColumn.name()).append("=?");
						}
						
						Object o = ms[i].invoke(obj);
						//如果设置了非空则判断值是否为空
						if(!annoColumn.nullable()){
							if(o==null){
								throw new CommonJdbcException("非空字段"+annoColumn.name()+"的实际值不能为空");
							}
						}
						processAnnoColumn(args,annoColumn,o);
						argTypes.add(ms[i].getReturnType());
					}else{
						whereSql = " where "+annoColumn.name()+"=?";
						idValue = ms[i].invoke(obj);
						returnIdValue = ms[i].getReturnType();
						idFlag++;
					}
				}
			}
		}
		
		if(idFlag==0)
			throw new CommonJdbcException("未发现有效的主键定义无法完成更新(@Column(id=?))");
		if(args.size()==0)
			throw new CommonJdbcException("未发现有效的数据库映射字段名(@Column(name=?))");
		if(args.size()!=argTypes.size())
			throw new CommonJdbcException("数据格式与类型不匹配");
		
		Object[] arguments = new Object[args.size()+1];
		Object[] argumentTypes = new Object[argTypes.size()+1];
		for(int i=0;i<args.size();i++){
			arguments[i] = args.get(i);
			argumentTypes[i] = argTypes.get(i);
		}
		argumentTypes[args.size()] = returnIdValue;
		arguments[args.size()] = idValue;
		
		sqlColumn.append(whereSql);
		
		PreparedStatement ps = null;
		try{
			String sqlx = sqlColumn.toString().toUpperCase();
			if(showSql)
				logger.info("sql="+sqlx);
			ps = con.prepareStatement(sqlx);
			processSql3(ps,argumentTypes,arguments);
			ps.executeUpdate();
		}catch(SQLException e){
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		}finally{
			releasePS(ps);
		}
	}
	
	private void saveOrUpdate__(Connection con, Object obj)throws InstantiationException,IllegalAccessException,InvocationTargetException,SecurityException, NoSuchMethodException{
		Class c = obj.getClass();
		Method[] ms = c.getMethods();
		int j=0;
		for(int i=0;i<ms.length;i++){
			String methodName = ms[i].getName();
			if(methodName.startsWith("get")){
				Column annoColumn = ms[i].getAnnotation(Column.class);
				if(annoColumn!=null&&annoColumn.name()!=null&&annoColumn.name().length()>0){
					if(annoColumn.id()){
						if(j>0) 	throw new CommonJdbcException("实体存在多个ID对象！");
						j++;
						Object o = ms[i].invoke(obj);
							if(o==null){
								save__(con,obj);
							}else{
								//获取表名
								Table annoTable = (Table)c.getAnnotation(Table.class);
								String tableName = annoTable.name();
								String sql="select count(1)  from "+tableName+" where "+annoColumn.name()+" =?";
								Long l=queryObject(sql, Long.class, o);
								if(l>0){
									update__(con,obj);
								}else{
									save__(con,obj);
								}
								
							}
					}
					if(j==0) save__(con,obj);
				}
			}
		}
	}
	
	private int callx__(Object obj){
		Connection con = null;
		try {
			con = getConnection();
			return call__(con,obj);
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		} finally{
			releaseConnection(con);
		}
	}
	private int callx__(String spName,Object obj){
		Connection con = null;
		try {
			con = getConnection();
			return call__(con,spName,obj);
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		} finally{
			releaseConnection(con);
		}
	}
	private int callx__(Connection con, Object obj){
		try {
			return call__(con,obj);
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		}
	}
	private int call_execute(Connection con, Object obj,String spName)  throws SQLException{
		if(con==null)
			return -1;
		CallableStatement callStm = null;
		try {
			StringBuffer sql = new StringBuffer();
			
			String callName = spName;
			sql.append("{call ").append(callName).append("(");
			
			Field[] fs = getAllFields(obj.getClass());
			int flag = 0;
			int isOut = 0;
			if(fs!=null&&fs.length>0){
				//for1
				//连接调用过程语句
				for(int i=0;i<fs.length;i++){
					In in = fs[i].getAnnotation(In.class);
					Out out = fs[i].getAnnotation(Out.class);
					if(in!=null||out!=null){
						if(in!=null&&out!=null)
							throw new CommonJdbcException("调用参数定义重复(@In&@Out)");
						flag++;
						int order = 0, inorout=0;
						if(in!=null){
							order = in.order();
							inorout = 1;
						}
						if(out!=null){
							order = out.order();
							inorout = 2;
							isOut++;
						}
						if(flag==1)
							sql.append("?");
						else
							sql.append(",?");
					}
				}
				
				sql.append(")}");
				String sqlx = sql.toString().toUpperCase();
				if(showSql)
					logger.info("sql="+sqlx);
				callStm = con.prepareCall(sqlx);
				
				//for2
				//绑定调用过程中in类型的参数
				for(int i=0;i<fs.length;i++){
					In in = fs[i].getAnnotation(In.class);
					Out out = fs[i].getAnnotation(Out.class);
					if(in!=null||out!=null){
						if(in!=null&&out!=null)
							throw new CommonJdbcException("调用参数定义重复(@In&@Out)");
						int order = 0, inorout=0;
						if(in!=null){
							order = in.order();
							inorout = 1;
						}
						if(out!=null){
							order = out.order();
							inorout = 2;
						}
						
						String fieldName = fs[i].getName();
						Method method = obj.getClass().getMethod("get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length()));
						if(method==null)
							throw new CommonJdbcException("属性:"+fs[i].getName()+"缺少get方法");
						Object value = method.invoke(obj);
						processCallableStatement(callStm,order,inorout,fs[i],value);
					}
				}
				
				//执行调用
				int b = callStm.executeUpdate();
				if(b<0)
					return -1;
				
				//for3
				//处理调用过程返回结果的绑定
				if(isOut>0){
					processResutlByCallabel(callStm,obj);
				}
				return 1;
			}
			return -2;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e){
			e.printStackTrace();
		} finally{
			releaseCallable(callStm);
		}
		return -3;
	}
	private int call__(Connection con, String spName,Object obj) throws SQLException{
		return call_execute(con, obj, spName);
	}
	private int call__(Connection con, Object obj) throws SQLException{
		Callable callable = obj.getClass().getAnnotation(Callable.class);
		if(callable==null||callable.name()==null||callable.name().length()==0)
			throw new CommonJdbcException("未发现有效的调用过程名(@Callable(name=?))");
		String callName = callable.name();
		return call_execute(con, obj, callName);
	}
	
	
	
	/**
	 *	根据sql，模板类，绑定变量参数查询
	 *	返回查询结果集
	 **/
	private <T> List query__(String sql,Class<T> clazz, Object... arguments){
		Connection con = null;
		try{
			con = getConnection();
			return queryBySql__(con,sql,clazz,arguments);
		}finally{
			releaseConnection(con);
		}
	}
	private <T> List query__(Connection con, String sql,Class<T> clazz, Object... arguments){
		return queryBySql__(con,sql,clazz,arguments);
	}
	private <T> List queryNT__(String sql,Class<T> clazz, Object... arguments){
		Connection con = null;
		try{
			//con = getConnection();
			con = dataSource.getConnection();
			if(con==null)
				throw new CommonJdbcException("获取数据库连接错误...");
			return queryBySql__(con,sql,clazz,arguments);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}finally{
			//releaseConnection(con);
			try {
				if(con!=null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *	从Connection中获取PreparedStatement
	 *	并根据绑定变量执行sql查询
	 *	从ResultSet中获取分析结果集
	 *  返回泛型的list结果集
	 **/
	private <T> List queryBySql__(Connection con, String sql, Class<T> clazz, Object... args){
		if(sql==null)
			return null;
		if(con==null)
			return null;
		List<T> resultList = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sqlx = sql.toUpperCase();
			ps = con.prepareStatement(sqlx,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			processSql(ps,args);
			//输出SQL
			if(showSql)
				logger.info("sql="+sqlx);
			long beginTime = System.currentTimeMillis();
			rs = ps.executeQuery();
			//如果返回结果集大于参数设置，则抛出异常
			if(maxsqlcount>0){
				rs.last();
				int rowcount = rs.getRow();
				if(rowcount>maxsqlcount){
					throw new CommonJdbcException("sqlcount("+rowcount+") is more than maxsqlcount("+maxsqlcount+")");
				}
				rs.beforeFirst();
			}
			//如果执行时间大于参数设置，则抛出异常
			if(maxsqltime>0){
				long endtime = System.currentTimeMillis()-beginTime;
				if(endtime>maxsqltime){
					throw new CommonJdbcException("sqltime("+endtime+") is more than maxsqltime("+maxsqltime+")");
				}
			}
			
			resultList = processResutlSet(rs,clazz);
			if(devMode)
				logger.info("sqltime="+(System.currentTimeMillis()-beginTime)+"ms,sqlcount="+resultList.size());
			return resultList;
		} catch (InstantiationException e) {
				e.printStackTrace();
		} catch (IllegalAccessException e) {
				e.printStackTrace();
		} catch (InvocationTargetException e) {
				e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CommonJdbcException(e.getMessage());
		} finally{
			releaseRSAndPS(rs,ps);
		}
		return null;
	}
	
	/**
	 *	执行sql
	 *	返回查询结果集
	 **/
	private int execute__(String sql){
		Connection con = null;
		try{
			con = getConnection();
			return execute__(con,sql);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			releaseConnection(con);
		}
		return -2;
	}
	private int execute__(Connection con, String sql){
		if(con==null)
			return -1;
		Statement stm = null;
		try{
			stm = con.createStatement();
			String sqlx = sql.toUpperCase();
			if(showSql)
				logger.info("sql="+sqlx);
			boolean b = stm.execute(sqlx);
			return b==true?1:-1;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			releaseStatement(stm);
		}
		return -2;
	}
	
	/**
	 *	根据绑定变量，执行sql
	 *	返回查询结果集
	 **/
	private int execute__(String sql, Object...args){
		Connection con = null;
		try{
			con = getConnection();
			return execute__(con,sql,args);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			releaseConnection(con);
		}
		return -2;
	}
	private int execute__(Connection con, String sql, Object...args){
		if(con==null)
			return -1;
		PreparedStatement ps = null;
		try{
			String sqlx = sql.toUpperCase();
			if(showSql)
				logger.info("sql="+sqlx);
			ps = con.prepareStatement(sqlx);
			processSql(ps,args);
			int count = ps.executeUpdate();
			return count;
		}catch(SQLException e){
			//e.printStackTrace();
			throw new CommonJdbcException(e);
		}finally{
			releasePS(ps);
		}
		//return -2;
	}
	
	/**
	 *	根据绑定变量个数进行sql变量绑定
	 **/
	private void processSql(PreparedStatement ps, Object... args) throws SQLException{
		if(args!=null){
			for(int i=0;i<args.length;i++){
				Object arg = args[i];
				if(arg!=null){
					if(arg.getClass().equals(java.lang.String.class)){
						ps.setString(i+1, (String)arg);
					}else if(arg.getClass().equals(java.lang.Boolean.class)||arg.getClass().equals(boolean.class)){
						ps.setBoolean(i+1, (Boolean)arg);
					}else if(arg.getClass().equals(java.lang.Integer.class)||arg.getClass().equals(int.class)){
						ps.setInt(i+1, (Integer)arg);
					}else if(arg.getClass().equals(java.lang.Long.class)||arg.getClass().equals(long.class)){
						ps.setLong(i+1, (Long)arg);
					}else if(arg.getClass().equals(java.lang.Float.class)||arg.getClass().equals(float.class)){
						ps.setFloat(i+1, (Float)arg);
					}else if(arg.getClass().equals(java.lang.Double.class)||arg.getClass().equals(double.class)){
						ps.setDouble(i+1, (Double)arg);
					}else if(arg.getClass().equals(java.sql.Date.class)){
						ps.setDate(i+1, (java.sql.Date)arg);
					}else if(arg.getClass().equals(java.sql.Time.class)){
						ps.setTime(i+1, (java.sql.Time)arg);
					}else if(arg.getClass().equals(java.sql.Timestamp.class)){
						ps.setTimestamp(i+1, (java.sql.Timestamp)arg);
					}else if(arg.getClass().equals(byte[].class)){
						ps.setBytes(i+1, (byte[])arg);
					}else if(arg.getClass().equals(java.util.Date.class)){
						java.sql.Date date = new java.sql.Date(((java.util.Date)arg).getTime());
						ps.setDate(i+1, (java.sql.Date)date);
					}else{
						throw new CommonJdbcException("未知的绑定变量类型:"+arg.getClass()+"!");
					}
				}else{
					boolean using = false;
					int sqlType = Types.NULL;
					try{
						sqlType = ps.getParameterMetaData().getParameterType(i+1);
					}catch(Throwable ex){
						//ex.printStackTrace();
						DatabaseMetaData dbmd = ps.getConnection().getMetaData();
						String databaseProductName = dbmd.getDatabaseProductName();
						String jdbcDriverName = dbmd.getDriverName();
						if (databaseProductName.startsWith("Informix") ||
								jdbcDriverName.startsWith("Microsoft SQL Server")) {
							using = true;
						}else if (databaseProductName.startsWith("DB2") ||
								jdbcDriverName.startsWith("jConnect") ||
								jdbcDriverName.startsWith("SQLServer")||
								jdbcDriverName.startsWith("Apache Derby")) {
							sqlType = Types.VARCHAR;
						}
					}
					if(using){
						ps.setObject(i+1,null);
					}else{
						ps.setNull(i+1, sqlType);
					}
					
					//如果数据库类型Informix,Microsoft SQL Server,DB2.,jConnect,SQLServer,Apache Derby
					//中的一个则会抛出Throwable异常
					/*
					DatabaseMetaData dbmd = ps.getConnection().getMetaData();
					String databaseProductName = dbmd.getDatabaseProductName();
					String jdbcDriverName = dbmd.getDriverName();
					if (databaseProductName.startsWith("Informix") ||
							jdbcDriverName.startsWith("Microsoft SQL Server")) {
						useSetObject = true;
					}
					else if (databaseProductName.startsWith("DB2") ||
							jdbcDriverName.startsWith("jConnect") ||
							jdbcDriverName.startsWith("SQLServer")||
							jdbcDriverName.startsWith("Apache Derby")) {
						sqlType = Types.VARCHAR;
					}
					*/
				}
			}
		}
	}
	private void processSql2(PreparedStatement ps, String... args) throws SQLException{
		if(args!=null){
			for(int i=0;i<args.length;i++){
				Object arg = args[i];
				if(arg!=null){
					ps.setString(i+1, (String)arg);
				}else{
					ps.setNull(i+1, Types.VARCHAR);
				}
			}
		}
	}
	private void processSql3(PreparedStatement ps, Object[] types, Object[] args) throws SQLException{
		if(args!=null){
			for(int i=0;i<args.length;i++){
				Object arg = args[i];
				Object type = types[i];
				
				if(java.lang.String.class.equals(type)){
					if(arg!=null)
						ps.setString(i+1, (String)arg);
					else
						ps.setNull(i+1, Types.VARCHAR);
				}else if(java.lang.Integer.class.equals(type)||java.lang.Byte.class.equals(type)||java.lang.Short.class.equals(type)
						||int.class.equals(type)||byte.class.equals(type)||short.class.equals(type)){
					if(arg!=null)
						ps.setInt(i+1, (Integer)arg);
					else
						ps.setNull(i+1, Types.INTEGER);
				}else if(java.lang.Boolean.class.equals(type)||boolean.class.equals(type)){
					if(arg!=null)
						ps.setBoolean(i+1, (Boolean)arg);
					else
						ps.setNull(i+1, Types.BOOLEAN);
				}else if(java.lang.Long.class.equals(type)||long.class.equals(type)){
					if(arg!=null)
						ps.setLong(i+1, (Long)arg);
					else
						ps.setNull(i+1, Types.INTEGER);
				}else if(java.lang.Float.class.equals(type)||float.class.equals(type)){
					if(arg!=null)
						ps.setFloat(i+1, (Float)arg);
					else
						ps.setNull(i+1, Types.FLOAT);
				}else if(java.lang.Double.class.equals(type)||double.class.equals(type)){
					if(arg!=null)
						ps.setFloat(i+1, (Float)arg);
					else
						ps.setNull(i+1, Types.DOUBLE);
				}else if(java.sql.Date.class.equals(type)){
					if(arg!=null)
						ps.setDate(i+1, (java.sql.Date)arg);
					else
						ps.setNull(i+1, Types.DATE);
				}else if(java.sql.Time.class.equals(type)){
					if(arg!=null)
						ps.setTime(i+1, (java.sql.Time)arg);
					else
						ps.setNull(i+1, Types.TIME);
				}else if(java.sql.Timestamp.class.equals(type)){
					if(arg!=null)
						ps.setTimestamp(i+1, (java.sql.Timestamp)arg);
					else
						ps.setNull(i+1, Types.TIMESTAMP);
				}else if(java.util.Date.class.equals(type)){
					if(arg!=null){
						java.sql.Timestamp timestamp = new java.sql.Timestamp(((java.util.Date)arg).getTime());
						ps.setTimestamp(i+1, timestamp);
					}else
						ps.setNull(i+1, Types.DATE);
				} else
					try {
						if(Class.forName("[B").equals(type)){
							if(arg!=null){
								ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) arg);
								ps.setBinaryStream(i+1, bais,bais.available());
							}else
								ps.setNull(i+1, Types.BLOB);
						}
						else{
							throw new CommonJdbcException("未知的绑定变量类型:"+type+"!");
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}
	
	/**
	 *	根据元注释对象的属性类型和order顺序以及inorout类型进行CallableStatement对象的绑定
	 **/
	private void processCallableStatement(CallableStatement csm, int order, int inorout, Field field, Object value) throws SQLException{
		if(csm==null||field==null)
			return;
		Class type = field.getType();
		if(java.lang.String.class.equals(type)){
			if(inorout==1){
				csm.setString(order, value==null?"":(String)value);
			}else{
				csm.registerOutParameter(order, Types.VARCHAR);
			}
		}else if(java.lang.Integer.class.equals(type)||java.lang.Byte.class.equals(type)||java.lang.Short.class.equals(type)
				||int.class.equals(type)||byte.class.equals(type)||short.class.equals(type)){
			if(inorout==1){
				csm.setInt(order, value==null?0:(Integer)value);
			}else{
				csm.registerOutParameter(order, Types.INTEGER);
			}
		}else if(java.lang.Boolean.class.equals(type)||boolean.class.equals(type)){
			if(inorout==1){
				csm.setBoolean(order, value==null?true:(Boolean)value);
			}else{
				csm.registerOutParameter(order, Types.BOOLEAN);
			}
		}else if(java.lang.Long.class.equals(type)||long.class.equals(type)){
			if(inorout==1){
				csm.setLong(order, value==null?0:(Long)value);
			}else{
				csm.registerOutParameter(order, Types.INTEGER);
			}
		}else if(java.lang.Float.class.equals(type)||float.class.equals(type)){
			if(inorout==1){
				csm.setFloat(order, value==null?0:(Float)value);
			}else{
				csm.registerOutParameter(order, Types.FLOAT);
			}
		}else if(java.lang.Double.class.equals(type)||double.class.equals(type)){
			if(inorout==1){
				csm.setDouble(order, value==null?0:(Double)value);
			}else{
				csm.registerOutParameter(order, Types.DOUBLE);
			}
		}else if(java.sql.Date.class.equals(type)){
			if(inorout==1){
				csm.setDate(order, (java.sql.Date)value);
			}else{
				csm.registerOutParameter(order, Types.DATE);
			}
		}else if(java.sql.Time.class.equals(type)){
			if(inorout==1){
				csm.setTime(order, (java.sql.Time)value);
			}else{
				csm.registerOutParameter(order, Types.TIME);
			}
		}else if(java.sql.Timestamp.class.equals(type)){
			if(inorout==1){
				csm.setTimestamp(order, (java.sql.Timestamp)value);
			}else{
				csm.registerOutParameter(order, Types.TIMESTAMP);
			}
		}else if(java.util.Date.class.equals(type)){
			if(inorout==1){
				if(value!=null){
					java.sql.Date date = new java.sql.Date(((java.util.Date)value).getTime());
					csm.setDate(order, date);
				}else{
					csm.setDate(order, null);
				}
			}else{
				csm.registerOutParameter(order, Types.DATE);
			}
		}else{
			throw new CommonJdbcException("未知的绑定变量类型:"+type+"!");
		}
	}
	
	/**
	 *	根据ResultSet结果集和Class模板解析ResultSet
	 **/
	private <T> List processResutlSet(ResultSet rs, Class<T> clazz) throws InstantiationException,IllegalAccessException,InvocationTargetException,SQLException{
		List<T> resultList = new ArrayList<T>();
		if(rs!=null){
			while(rs.next()){
				if(Long.class.equals(clazz)||Integer.class.equals(clazz)
						||Byte.class.equals(clazz)||Blob.class.equals(clazz)||Clob.class.equals(clazz)
						||Short.class.equals(clazz)||Float.class.equals(clazz)
						||Double.class.equals(clazz)||Boolean.class.equals(clazz)
						||String.class.equals(clazz)||java.sql.Time.class.equals(clazz)
						||java.sql.Timestamp.class.equals(clazz)||java.sql.Date.class.equals(clazz)
						||java.util.Date.class.equals(clazz)||boolean.class.equals(clazz)
						||long.class.equals(clazz)||int.class.equals(clazz)
						||byte.class.equals(clazz)||short.class.equals(clazz)
						||double.class.equals(clazz)||float.class.equals(clazz)||byte[].class.equals(clazz)){
					if(Integer.class.equals(clazz)||int.class.equals(clazz)){
						resultList.add((T)(new Integer(rs.getInt(1))));
					}else if(Byte.class.equals(clazz)||byte.class.equals(clazz)){
						resultList.add((T)(new Byte(rs.getByte(1))));
					}else if(Boolean.class.equals(clazz)||boolean.class.equals(clazz)){
						resultList.add((T)(new Boolean(rs.getBoolean(1))));
					}else if(Short.class.equals(clazz)||short.class.equals(clazz)){
						resultList.add((T)(new Short(rs.getShort(1))));
					}else if(Long.class.equals(clazz)||long.class.equals(clazz)){
						resultList.add((T)(new Long(rs.getLong(1))));
					}else if(Float.class.equals(clazz)||float.class.equals(clazz)){
						resultList.add((T)(new Float(rs.getFloat(1))));
					}else if(Double.class.equals(clazz)||double.class.equals(clazz)){
						resultList.add((T)(new Double(rs.getDouble(1))));
					}else if(String.class.equals(clazz)){
						resultList.add((T)(rs.getString(1)));
					}else if(java.sql.Time.class.equals(clazz)){
						resultList.add((T)(rs.getTime(1)));
					}else if(java.sql.Date.class.equals(clazz)){
						resultList.add((T)(rs.getDate(1)));
					}else if(java.sql.Timestamp.class.equals(clazz)||java.util.Date.class.equals(clazz)){
						resultList.add((T)(rs.getTimestamp(1)));
					}else if(Clob.class.equals(clazz)){
						resultList.add((T)(rs.getClob(1)));
					}else if(Blob.class.equals(clazz)){
						resultList.add((T)(rs.getBlob(1)));
					}else if(byte[].class.equals(clazz)){
						resultList.add((T)(rs.getBytes(1)));
					}
					break;
				}
				
				Object obj = clazz.newInstance();
				clazz.getDeclaredMethods();
				ResultSetMetaData rsmd = rs.getMetaData();
				
				PropertyDescriptor[] objectProperties = null;
				if(propertiesCache.containsKey(obj.getClass())){
					objectProperties = propertiesCache.get(obj.getClass());
				}else{
					objectProperties = PropertyUtils.getPropertyDescriptors(obj);
				}
				for(int i=0;i<objectProperties.length;i++){
					Method setMehtod = objectProperties[i].getWriteMethod();
					Method getMethod = objectProperties[i].getReadMethod();
					String colName = objectProperties[i].getName();
					Object value = null;
					
					if(setMehtod!=null&&getMethod!=null&&colName!=null&&colName.length()>0){
						if(match(rsmd,colName)){
							Class type = getMethod.getReturnType();
							if(type.isPrimitive()){
								type = ClassUtils.primitiveToWrapper(type);
							}
							if(Integer.class.equals(type)){
								value = rs.getInt(colName);
							}else if (Long.class.equals(type)) {
								value = rs.getLong(colName);
							}else if (Short.class.equals(type)) {
								value = rs.getShort(colName);
							}else if(Double.class.equals(type)){
								value = rs.getDouble(colName);
							}else if(BigDecimal.class.equals(type)){
								value = rs.getBigDecimal(colName);
							}else if(Float.class.equals(type)){
								value = rs.getFloat(colName);
							}else if (String.class.equals(type)) {
								value = rs.getString(colName);
							}else if (java.sql.Date.class.equals(type)) {
								value = rs.getDate(colName);
							}else if (java.sql.Time.class.equals(type)) {
								value = rs.getTime(colName);
							}else if (java.sql.Timestamp.class.equals(type)) {
								value = rs.getTimestamp(colName);
							}else if (java.util.Date.class.equals(type)) {
								value = rs.getTimestamp(colName);
							}else if(byte[].class.equals(type)){
								value = rs.getBytes(colName);
							}else
								try {
									if (Class.forName("[B").equals(type)) {
										value = rs.getBlob(colName);
									}else{
										throw new CommonJdbcException("不支持的数据类型:"+colName+"["+type+"]");
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
						}
						if(value!=null)
							setMehtod.invoke(obj, value);
					}
				}
				resultList.add((T)obj);
			}
		}
		return resultList;
	}

	/**
	 *	根据CallableStatement对象处理调用返回对象
	 **/
	private void processResutlByCallabel(CallableStatement csm, Object object) throws InstantiationException,IllegalAccessException,InvocationTargetException,NoSuchMethodException,SQLException{
		if(csm==null||object==null)
			return;
		
		Field[] fs = getAllFields(object.getClass());
		for(int i=0;i<fs.length;i++){
			In in = fs[i].getAnnotation(In.class);
			Out out = fs[i].getAnnotation(Out.class);
			if(in!=null||out!=null){
				if(in!=null&&out!=null)
					throw new CommonJdbcException("调用参数定义重复(@In&@Out)");
				if(out!=null){
					//处理调用返回
					String fieldName = fs[i].getName();
					Method setMethod = object.getClass().getMethod("set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length()),fs[i].getType());

					Class type = fs[i].getType();
					Object value = null;
					int order = out.order();
					if(java.lang.String.class.equals(type)){
						value = csm.getString(order);
					}else if(java.lang.Integer.class.equals(type)||int.class.equals(type)){
						value = csm.getInt(order);
					}else if(java.lang.Byte.class.equals(type)||byte.class.equals(type)){
						value = csm.getByte(order);
					}else if(java.lang.Short.class.equals(type)||short.class.equals(type)){
						value = csm.getShort(order);
					}else if(java.lang.Boolean.class.equals(type)||boolean.class.equals(type)){
						value = csm.getBoolean(order);
					}else if(java.lang.Long.class.equals(type)||long.class.equals(type)){
						value = csm.getLong(order);
					}else if(java.lang.Float.class.equals(type)||float.class.equals(type)){
						value = csm.getFloat(order);
					}else if(java.lang.Double.class.equals(type)||double.class.equals(type)){
						value = csm.getDouble(order);
					}else if(java.sql.Date.class.equals(type)){
						value = csm.getDate(order);
					}else if(java.sql.Time.class.equals(type)){
						value = csm.getTime(order);
					}else if(java.sql.Timestamp.class.equals(type)||java.util.Date.class.equals(type)){
						value = csm.getTimestamp(order);
					}else if(Clob.class.equals(type)){
						value = csm.getClob(order);
					}else if(Blob.class.equals(type)){
						value = csm.getBlob(order);
					}else{
						throw new CommonJdbcException("未知的绑定变量类型:"+type+"!");
					}
					
					//调用源对象的set方法进行返回值的绑定
					setMethod.invoke(object, value);
				}
			}
		}
	}

	
	/**
	 *	从dataSource中获取数据库连接
	 *	返回获取的连接对接
	 *	返回null表示获取失败
	 **/
	private Connection getConnection(){
		return DataSourceUtils.getConnection(getDataSource());
	}
	/**
	 *	释放数据库连接到dataSource中
	 *	返回值1：释放成功
	 *	返回值<0：释放失败
	 **/
	private int releaseConnection(Connection con){
		DataSourceUtils.releaseConnection(con, getDataSource());
		return 1;
	}
	/**
	 *	释放Statement对象
	 *	返回值1：释放成功
	 *	返回值<0：释放失败
	 **/
	private int releaseStatement(Statement stm){
		try {
			if(stm!=null){
				stm.close();
				return 1;
			}
			return -1;
		} catch (SQLException e) {
			throw new CommonJdbcException(e.getMessage());
		}
	}
	/**
	 *	释放PreparedStatement和ResultSet对象
	 *	返回值1：释放成功
	 *	返回值<0：释放失败
	 **/
	private int releaseRSAndPS(ResultSet rs, PreparedStatement ps){
		try {
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
			return 1;
		} catch (SQLException e) {
			throw new CommonJdbcException(e.getMessage());
		}
	}
	/**
	 *	释放PreparedStatement和ResultSet对象
	 *	返回值1：释放成功
	 *	返回值<0：释放失败
	 **/
	private int releasePS(PreparedStatement ps){
		try {
			if(ps!=null){
				ps.close();
				return 1;
			}
			return -1;
		} catch (SQLException e) {
			throw new CommonJdbcException(e.getMessage());
		}
	}
	/**
	 *	释放CallableStatement对象
	 *	返回值1：释放成功
	 *	返回值<0：释放失败
	 **/
	
	private int releaseCallable(CallableStatement callable){
		try {
			if(callable!=null){
				callable.close();
				return 1;
			}
			return -1;
		} catch (SQLException e) {
			throw new CommonJdbcException(e.getMessage());
		}
	}
	/**
	 *	处理SQL，增加分页
	 *	返回值处理后的SQL
	 **/
	private String processSqlPage(String sql){
		if(sql==null)
			return null;
		if(databaseDriver.toUpperCase().equals("MYSQL")){
			return sql+" limit ?,?";
		}else{
			return "select * from (select page_object_.*,rownum page_rownum_ from ("+
				sql+") page_object_) where page_rownum_>=? and rownum<=?";
		}
	}
	
	/**
	 *	根据List进行动态处理绑定变量
	 **/
	private ArgBind processSqlBindArg(String sql, List<ArgMap> args){
		if(sql==null)
			return null;
		ArgBind bind = new ArgBind();
		bind.setSqlx(sql);
		bind.setArgs(null);
		if(args!=null){
			StringBuffer sqlBuffer = new StringBuffer(sql);
			Object[] argObjects = new Object[args.size()];
			if(sql.indexOf("where")<=0&&sql.indexOf("WHERE")<=0){
				sqlBuffer.append(" where 1=1 ");
			}
			for(int i=0;i<args.size();i++){
				ArgMap argObject = args.get(i);
				String key = argObject.getKey();
				sqlBuffer.append(" and ").append(key).append("=?");
				argObjects[i] = argObject.getValue();
			}
			bind.setSqlx(sqlBuffer.toString());
			bind.setArgs(argObjects);
			return bind;
		}
		return bind;
	}
	
	/**
	 *	检查元注释对象的中的长度和精度信息，并进行相应的处理
	 **/
	private void processAnnoColumn(List list, Column column, Object value){
		if(column!=null){
			if(column.length()>0&&value!=null&&value.getClass().equals(java.lang.String.class)){
				String v = (String)value;
				if(v!=null&&v.length()>column.length()){
					list.add(v.substring(0,column.length()));
				}else{
					list.add(value);
				}
			//}else if(column.precision()>0&&column.scale()==0){
			//	list.add(value);
			}else{
				list.add(value);
			}
		}
	}
	
	private boolean match(ResultSetMetaData rsmd,String colName) throws SQLException{
		int colCount =rsmd.getColumnCount();
		for(int i=0;i<colCount;i++){
			if(colName.equalsIgnoreCase(rsmd.getColumnName(i+1)))
				return true;
		}
		return false;
	}
	private Field[] getAllFields(Class clazz){
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
	public String getSequenceValue(String sequenceName){
		if(sequenceName==null)
			return null;
		String sql="select "+sequenceName+".nextval key from dual";
			
		List<ArgMap> resultList = query__(sql,ArgMap.class);
		if(resultList==null||resultList.size()==0)
			return null;
		return resultList.get(0).getKey();
	}
	
	public boolean getDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	
	public boolean getShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	
	public int getMaxsqltime() {
		return maxsqltime;
	}

	public void setMaxsqltime(int maxsqltime) {
		this.maxsqltime = maxsqltime;
	}

	public int getMaxsqlcount() {
		return maxsqlcount;
	}

	public void setMaxsqlcount(int maxsqlcount) {
		this.maxsqlcount = maxsqlcount;
	}
	

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		//BasicConfigurator.configure();
		this.dataSource = dataSource;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData meta = connection.getMetaData();
			databaseDriver = meta.getDatabaseProductName();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection!=null)
					connection.close();
			} catch (SQLException e) {
				//......
			}
		}
	}

}
