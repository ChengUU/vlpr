package com.sgc.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

/**
 * 
 * @author ChengXX 
 * getInstance()方法返回POOL的唯一实例，第一次调用时将执行构造函数
 * 构造函数Pool()调用驱动程序装在loadDriver()函数；连接池创建createPool()函数loadDrivers()装载驱动
 * createPool()建连接池getConection()返回一个连接实例getConnection(time)添加时间限制
 * freeConnection(Connection con)将连接实例返回到连接池getnum()返回空闲连接数
 * getnumActive()返回当前使用的链接数
 * 单例模式：静态实例对象、私有的构造函数、一个公开的获取实例对象的方法        
 */
public class Pool {
	private static Pool instance = null;
	// 最大连接数
	private int maxConnection = 100;
	// 保持连接数
	private int normalConnection = 10;
	// 密码
	private String password = "sun302758.";
	// 连接URL
	private String url = "jdbc:mysql://localhost:3306/lpr?useSSL=true";
	// 用户名
	private String user = "sa";
	// 数据库驱动程序
	private String driverName = "com.mysql.jdbc.Driver";
	private Driver driver;
	private DBConnectionPool pool = null;

	// 将构造函数定义为私有，不允许外界访问
	private Pool() {
		loadDriver(driverName);
		createPool();
	}

	// 装载和注册JDBC驱动程序
	private void loadDriver(String driverName) {
		String driverClassName = driverName;
		try {
			driver = (Driver) Class.forName(driverClassName).newInstance();

			DriverManager.registerDriver(driver);
			System.out.println("成功注册JDBC驱动程序:" + driverClassName);
		} catch (ClassNotFoundException e) {

			System.out.println("无法注册JDBC驱动程序:" + driverClassName + ",错误:" + e);
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("无法注册JDBC驱动程序:" + driverClassName + ",错误:" + e);
		} catch (SQLException e) {
			System.out.println("无法注册JDBC驱动程序:" + driverClassName + ",错误:" + e);
		}
	}

	// 创建连接池
	private void createPool() {
		pool = new DBConnectionPool(password, url, user, normalConnection,
				maxConnection);
		if(null!=pool){
			System.out.println("连接池创建成功......");
		}else{
			System.out.println("连接池创建失败......");
		}
	}
	//获取连接池实例
	public static synchronized Pool getInstance(){
		if(null==instance) instance=new Pool();
		return instance;
	}
	//获得一个可用的连接，如果没有创建一个连接，且小于最大连接限制
	public Connection getConnection(){
		if(null!=pool){
			return pool.getConnection();
		}
		return null;
	}
	//获得一个连接有时间限制
	public Connection getConnection(long time){
		if(null!=pool){
			return pool.getConnection(time);
		}
		return null;
	}
	//将连接对象返回给连接池
	public void freeConnection(Connection con){
		if(null!=pool) pool.freeConnection(con);
	}
	//返回当前空连接数
	public int getNum(){
		return pool.getNum();
	}
	//返回当前连接数
	public int getNumActive(){
		return pool.getNumActive();
	}
	//关闭所有连接，撤销驱动注册
	public synchronized void release(){
		//关闭连接
		pool.release();
		//撤销驱动
		try{
			DriverManager.deregisterDriver(driver);
			System.out.println("撤销JDBC驱动程序"+driver.getClass().getName());
		}catch(SQLException e){
			System.out.println("无法撤销JDBC驱动程序"+driver.getClass().getName());
		}
	}
	
}
