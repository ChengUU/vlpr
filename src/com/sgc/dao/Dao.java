package com.sgc.dao;

import java.util.List;


public interface Dao<T>
{
	public boolean doInsert(T user);
	public T findByUid(int uid)throws Exception;
	public List<T> findAll()throws Exception;
	public boolean update(T obj);
	public boolean delete(int id);
	
}
