package com.sgc.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DBConnectionPool {
	private int checkedOut;
	private List<Connection> freeConnections=new LinkedList<Connection>();
	private int maxCon;
	private int normalCon;
	private String password;
	private String url;
	private String user;
	//空闲连接数
	private static int num=0;
	//当前连接数
	private static int numActive=0;

	public DBConnectionPool(String password, String url, String user,
			int normalConnection, int maxConnection) {
		this.password=password;
		this.url=url;
		this.user=user;
		this.normalCon=normalConnection;
		this.maxCon=maxConnection;
		initFreeCon();
		
	}
	//初始normal个连接
	private void initFreeCon(){
		for(int i=0;i<normalCon;i++){
			Connection c=newConnection();
			if(null!=c){
				freeConnections.add(c);
				num++;
			}
		}
	}
	//创建一个新连接
	private Connection newConnection() {
		Connection c=null;
		try{
			if(null==user) c=DriverManager.getConnection(url);
			else c=DriverManager.getConnection(url, user, password);
			System.out.println("连接池创建一个新的连接");
		}catch(SQLException e){
			System.out.println("无法创建这个URL连接"+url);
		}
		return c;
	}
	//获取当前的空闲连接
	public static final int getNum() {
		return num;
	}
	//获取当前的连接数
	public static final int getNumActive() {
		return numActive;
	}
	public synchronized Connection getConnection() {
		Connection c=null;
		//还有空闲连接
		if(freeConnections.size()>0){
			num--;
			c=(Connection)freeConnections.remove(0);
			try{
				if(c.isClosed()){
					System.out.println("删除无效连接");
					c=getConnection();
				}
			}catch(SQLException e){
				System.out.println("从连接池删除一个无效连接");
				c=getConnection();
			}
		}else if(0==maxCon||checkedOut<maxCon){
			//没有空闲连接且当前连接小于最大允许值，最大值为0则不限制
			c=newConnection();
		}
		//当前连接数+1
		if(null!=c) checkedOut++;
		numActive++;
	
		return c;
	}

	public synchronized Connection getConnection(long time) {
		Connection c=null;
		long startTime=new Date().getTime();
		while(null==(c=getConnection())){
			try{
				wait(time);
			}catch(InterruptedException e){}
			if((new Date().getTime()-startTime)>=time) return c=null;
		}
		return c;
	}
	//释放不用的链接
	public synchronized void freeConnection(Connection con) {
		freeConnections.add(con);
		num++;
		checkedOut--;
		numActive--;
		notifyAll();
	}
	//关闭所有连接
	public synchronized void release(){
		Iterator<Connection> ite=freeConnections.iterator();
		Connection c;
		while(ite.hasNext()){
			c=ite.next();
			try{
				c.close();
				num--;
			}catch(SQLException e){
				System.out.println("无法关闭连接池中的连接");
			}
		}
		freeConnections.clear();
		numActive=0;
	}
}
