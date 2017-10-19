package com.wb.jdbcutils;

import java.sql.Connection;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.wb.jdbcutils.args.ArgMap;


public class CommonJdbcUtils implements ApplicationContextAware{
	private static ApplicationContext context;
	private static CommonJdbcCore commonJdbcCore;
	
	public CommonJdbcUtils(CommonJdbcCore commonJdbcCore){
		this.commonJdbcCore = commonJdbcCore;
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}
	
	
	/*****************************************************************************************
	 *call start
	 *****************************************************************************************/
	public static int call(Object object){
		return commonJdbcCore.call(object);
	}
	public static int call(String spName,Object object){
		return commonJdbcCore.call(spName,object);
	}
	public static int call(Connection con, Object object){
		return commonJdbcCore.call(con,object);
	}
	/*****************************************************************************************
	 *call end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *execute start
	 *****************************************************************************************/
	public static int execute(String sql, Object...args){
		return commonJdbcCore.execute(sql, args);
	}
	public static int execute(Connection con, String sql, Object...args){
		return commonJdbcCore.execute(con,sql,args);
	}
	public static int execute(String sql){
		return commonJdbcCore.execute(sql);
	}
	public static int execute(Connection con, String sql){
		return commonJdbcCore.execute(con,sql);
	}
	public static int executeProcedure(String name, Object...args){
		return commonJdbcCore.executeProcedure(name, args);
	}
	/*****************************************************************************************
	 *execute end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *update begin
	 *****************************************************************************************/
	public static void update(Object obj){
		commonJdbcCore.update(obj);
	}
	public static void update(Connection con, Object obj){
		commonJdbcCore.update(con, obj);
	}
	public static <T> void update(List<T> list){
		commonJdbcCore.update(list);
	}
	public static <T> void update(Connection con, List<T> list){
		commonJdbcCore.update(con, list);
	}
	/*****************************************************************************************
	 *update end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *save begin
	 *****************************************************************************************/
	public static void save(Object obj){
		commonJdbcCore.save(obj);
	}
	public static void saveOrUpdate(Object obj){
		commonJdbcCore.saveOrUpdate(obj);
	}
	public static void save(Connection con, Object obj){
		commonJdbcCore.save(con, obj);
	}
	public static <T> void save(List<T> list){
		commonJdbcCore.save(list);
	}
	public static <T> void save(Connection con, List<T> list){
		commonJdbcCore.save(con, list);
	}
	/*****************************************************************************************
	 *save end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *query begin
	 *****************************************************************************************/
	public static <T> List query(Connection con,String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.query(con,sql, clazz, arguments);
	}
	public static <T> List query(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.query(con, sql, clazz, arguments);
	}
	public static <T> List query(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.query(sql, clazz, arguments);
	}
	public static <T> List query(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.query(sql, clazz, arguments);
	}
	public static <T> List queryNT(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryNT(sql, clazz, arguments);
	}
	public static <T> List queryNT(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryNT(sql, clazz, arguments);
	}
	/*****************************************************************************************
	 *query end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *queryPage begin
	 *****************************************************************************************/
	public static void queryPage(Connection con, Page page,String sql,Class clazz,Object... arguments){
		commonJdbcCore.queryPage(con, page, sql, clazz, arguments);
	}
	public static void queryPage(Connection con, Page page, String sql, Class clazz, List<ArgMap> arguments){
		commonJdbcCore.queryPage(con, page, sql, clazz, arguments);
	}
	public static void queryPage(Page page, String sql,Class clazz,Object... arguments){
		commonJdbcCore.queryPage(page, sql, clazz, arguments);
	}
	public static void queryPage(Page page,String sql, Class clazz, List<ArgMap> arguments){
		commonJdbcCore.queryPage(page, sql, clazz, arguments);
	}
	public static void queryPageNT(Page page, String sql,Class clazz,Object... arguments){
		commonJdbcCore.queryPageNT(page, sql, clazz, arguments);
	}
	public static void queryPageNT(Page page,String sql, Class clazz, List<ArgMap> arguments){
		commonJdbcCore.queryPageNT(page, sql, clazz, arguments);
	}
	/*****************************************************************************************
	 *queryPage end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *queryFirst begin
	 *****************************************************************************************/
	public static <T> T queryFirst(Connection con,String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryFirst(con,sql, clazz, arguments);
	}
	public static <T> T queryFirst(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryFirst(con, sql, clazz, arguments);
	}
	public static <T> T queryFirst(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryFirst(sql, clazz, arguments);
	}
	public static <T> T queryFirstNT(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryFirstNT(sql, clazz, arguments);
	}
	public static <T> T queryFirst(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryFirst(sql, clazz, arguments);
	}
	public static <T> T queryFirstNT(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryFirstNT(sql, clazz, arguments);
	}
	/*****************************************************************************************
	 *queryFirst end
	 *****************************************************************************************/
	
	/*****************************************************************************************
	 *queryObject begin
	 *****************************************************************************************/
	public static <T> T queryObject(Connection con,String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryObject(con,sql, clazz, arguments);
	}
	public static <T> T queryObject(Connection con, String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryObject(con, sql, clazz, arguments);
	}
	public static <T> T queryObject(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryObject(sql, clazz, arguments);
	}
	
	public static <T> T queryObject(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryObject(sql, clazz, arguments);
	}
	public static <T> T queryObjectNT(String sql,Class<T> clazz,Object... arguments){
		return commonJdbcCore.queryObjectNT(sql, clazz, arguments);
	}
	
	public static <T> T queryObjectNT(String sql, Class<T> clazz, List<ArgMap> arguments){
		return commonJdbcCore.queryObjectNT(sql, clazz, arguments);
	}
	/*****************************************************************************************
	 *queryObject end
	 *****************************************************************************************/
	
	public static CommonJdbcCore getCommonJdbcCore() {
		return commonJdbcCore;
	}

	public static void setCommonJdbcCore(CommonJdbcCore commonJdbcCore) {
		CommonJdbcUtils.commonJdbcCore = commonJdbcCore;
	}
	public static String getSequenceValue(String sequenceName){
		return commonJdbcCore.getSequenceValue(sequenceName);
	}
	
}
