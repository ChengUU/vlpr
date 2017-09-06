package com.sgc.bean;

public class User {
	private int uid;
	private String name;
	private String password;

	// 无参构造函数
	public User()
	{
		super();
	}

	public User(int uid,String name, String password)
	{
		this.uid = uid;
		this.name=name;
		this.password = password;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	@Override
	public String toString()
	{
		StringBuffer strb=new StringBuffer();
		strb.append("[uid="+this.uid);
		strb.append(",name="+this.name);
		strb.append(",password"+this.password+"]");
		
		return strb.toString();
	}

}
