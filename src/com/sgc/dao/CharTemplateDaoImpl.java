package com.sgc.dao;

import com.sgc.bean.CharTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class CharTemplateDaoImpl implements CharTemplateDao {

    //连接对象
    protected Connection conn=null;

    protected PreparedStatement pst=null;

    public CharTemplateDaoImpl(Connection conn) {this.conn=conn;}

    public boolean doInsert(CharTemplate charTemplate)
    {
        //操作是否成功的标志
        boolean flag=false;
        try{

            String insert = "insert into chartemplate(typeid,width,height,path,repchar) values(?,?,?,?,?)";
            pst=conn.prepareStatement(insert);
            pst.setObject(1,charTemplate.getTypeId());
            pst.setObject(2, charTemplate.getWidth());
            pst.setObject(3,charTemplate.getHeight());
            pst.setObject(4, charTemplate.getPath());
            pst.setObject(5,charTemplate.getRepChar());
            int n=pst.executeUpdate();
            this.pst.close();
            if(n>0) flag=true;
        }catch(Exception e){flag=false;}
        return flag;
    }

    public List<CharTemplate> findAll()throws Exception
    {
        String select="select * from chartemplate";
        //结果表
        List list=new ArrayList();
        this.pst=this.conn.prepareStatement(select);
        ResultSet rst=this.pst.executeQuery();
        while(rst.next())
        {
            CharTemplate charTemplate=new CharTemplate();
            charTemplate.setId(rst.getInt("id"));
            charTemplate.setTypeId(rst.getInt("typeid"));
            charTemplate.setWidth(rst.getInt("width"));
            charTemplate.setHeight(rst.getInt("height"));
            charTemplate.setPath(rst.getString("path"));
            charTemplate.setRepChar(rst.getString("repchar"));
            list.add(charTemplate);
        }
        //当关闭pst的时候，认识他结果集也会随之关闭
        this.pst.close();

        return list;
    }

    //通过id查找用户
    @Override
    public CharTemplate findByUid(int id)throws Exception
    {
        String sql="select * from chartemplate where id=?";
        this.pst=this.conn.prepareStatement(sql);
        this.pst.setObject(1,id);
        ResultSet rst=this.pst.executeQuery();
        CharTemplate charTemplate=null;
        if(rst.next())
        {
            charTemplate=new CharTemplate();
            charTemplate.setId(rst.getInt("id"));
            charTemplate.setTypeId(rst.getInt("typeid"));
            charTemplate.setWidth(rst.getInt("width"));
            charTemplate.setHeight(rst.getInt("height"));
            charTemplate.setPath(rst.getString("path"));
            charTemplate.setRepChar(rst.getString("repchar"));
        }
        this.pst.close();
        return charTemplate;
    }

    @Override
    public boolean update(CharTemplate obj) {
        //操作是否成功的标志
        boolean flag=false;
        try{
            String insert = "update chartemplate set typeid=?,width=?,height=?,path=?,repchar=? where id=?";
            pst=conn.prepareStatement(insert);
            pst.setObject(1,obj.getTypeId());
            pst.setObject(2, obj.getWidth());
            pst.setObject(3, obj.getHeight());
            pst.setObject(4,obj.getPath());
            pst.setObject(5, obj.getRepChar());
            pst.setObject(6, obj.getId());
            int n=pst.executeUpdate();
            pst.close();
            if(n>0) flag=true;
        }catch(Exception e){flag=false;e.printStackTrace();}
        return flag;
    }

    @Override
    public boolean delete(int id) {
        //操作是否成功的标志
        boolean flag=false;
        try{

            String insert = "delete from chartemplate where id=?";
            pst=conn.prepareStatement(insert);
            pst.setObject(1,id);
            int n=pst.executeUpdate();
            this.pst.close();
            if(n>0) flag=true;
        }catch(Exception e){flag=false;}
        return flag;
    }

    public List<CharTemplate> queryByPath(String path)throws SQLException {
        String select="select * from chartemplate where path=?";
        //结果表
        List list=new ArrayList();
        this.pst=this.conn.prepareStatement(select);
        pst.setObject(1,path);
        ResultSet rst=this.pst.executeQuery();
        while(rst.next())
        {
            CharTemplate charTemplate=new CharTemplate();
            charTemplate.setId(rst.getInt("id"));
            charTemplate.setTypeId(rst.getInt("typeid"));
            charTemplate.setWidth(rst.getInt("width"));
            charTemplate.setHeight(rst.getInt("height"));
            charTemplate.setPath(rst.getString("path"));
            charTemplate.setRepChar(rst.getString("repchar"));
            list.add(charTemplate);
        }
        //当关闭pst的时候，认识他结果集也会随之关闭
        this.pst.close();
        return list;
    }

    @Override
    public List<CharTemplate> queryByType(int type) throws SQLException {
        String select="select * from chartemplate where typeid=?";
        //结果表
        List list=new ArrayList();
        this.pst=this.conn.prepareStatement(select);
        pst.setObject(1,type);
        ResultSet rst=this.pst.executeQuery();
        while(rst.next())
        {
            CharTemplate charTemplate=new CharTemplate();
            charTemplate.setId(rst.getInt("id"));
            charTemplate.setTypeId(rst.getInt("typeid"));
            charTemplate.setWidth(rst.getInt("width"));
            charTemplate.setHeight(rst.getInt("height"));
            charTemplate.setPath(rst.getString("path"));
            charTemplate.setRepChar(rst.getString("repchar"));
            list.add(charTemplate);
        }
        //当关闭pst的时候，认识他结果集也会随之关闭
        this.pst.close();
        return list;
    }
}
