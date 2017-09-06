package com.sgc.dbcon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection
{
	//数据库驱动程序
	private final String driver="com.mysql.jdbc.Driver";
	//数据库URL
	private final String url="jdbc:mysql://localhost:3306//lpr";
	//用户名
	private final String dbUser="sa";
	//用户密码
	private final String dbPassword="sun302758.";

	private Connection conn=null;


	//构造函数初始化——数据库驱动程序的加载、数据库连接
	public DBConnection(){
		try{
			//加载数据库驱动程序
			Class.forName(driver);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
			System.out.println("数据库驱动程序加载失败......");
		}
		try{
			//取得数据库连接
			this.conn=DriverManager.getConnection(url,dbUser,dbPassword);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("数据库连接失败......");
		}
	}

	//获得数据库连接
	public Connection getConnection()
	{
		return this.conn;
	}

	//关闭数据库连接
	public void closeConnection()
	{
		try{
			this.conn.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("数据库关闭异常......");
		}
	}

}
