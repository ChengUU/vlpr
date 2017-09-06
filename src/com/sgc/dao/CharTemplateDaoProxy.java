package com.sgc.dao;

import com.sgc.bean.CharTemplate;
import com.sgc.bean.Pool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class CharTemplateDaoProxy implements CharTemplateDao {
    private Pool pool=Pool.getInstance();
    private CharTemplateDaoImpl dao=null;
    private Connection dbConn=null;

    public CharTemplateDaoProxy()
    {
        this.dbConn= pool.getConnection();
        this.dao=new CharTemplateDaoImpl(this.dbConn);
    }

    @Override
    public boolean doInsert(CharTemplate user)
    {
        boolean flag=false;
        try{
            //保存插入操作是否成功
            flag=this.dao.doInsert(user);
        }catch(Exception e){e.printStackTrace();}
        finally{
            pool.freeConnection(dbConn);
        }
        return flag;
    }
    public List<CharTemplate> findAll()
    {
        List<CharTemplate> list=null;
        try{
            list=this.dao.findAll();
        }catch(Exception e){e.printStackTrace();}
        finally{
            //关闭数据库连接
            pool.freeConnection(dbConn);
        }
        return list;
    }

    @Override
    public boolean update(CharTemplate obj) {return  dao.update(obj);}

    @Override
    public CharTemplate findByUid(int uid)
    {
        CharTemplate user=null;
        try{
            user=dao.findByUid(uid);
        }catch(Exception e){e.printStackTrace();}
        return user;
    }

    @Override
    public boolean delete(int id) {
        return dao.delete(id);
    }

    public List<CharTemplate> queryByPath(String path) {
        List<CharTemplate> list=null;
        try {
            list= dao.queryByPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<CharTemplate> queryByType(int type) {
        List<CharTemplate> list=null;
        try{
            list=dao.queryByType(type);
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
}
