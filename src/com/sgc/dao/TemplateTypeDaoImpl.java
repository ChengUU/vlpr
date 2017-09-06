package com.sgc.dao;

import com.sgc.bean.TemplateType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class TemplateTypeDaoImpl implements TemplateTypeDao {

    //连接对象
    protected Connection conn=null;

    protected PreparedStatement pst=null;

    public TemplateTypeDaoImpl(Connection conn) {this.conn=conn;}



    @Override
    public boolean delete(int id) {
        //操作是否成功的标志
        boolean flag=false;
        try{

            String insert = "delete from templatetype where id=?";
            pst=conn.prepareStatement(insert);
            pst.setObject(1,id);
            int n=pst.executeUpdate();
            this.pst.close();
            if(n>0) flag=true;
        }catch(Exception e){flag=false;}
        return flag;
    }

    public boolean doInsert(TemplateType templateType)
    {
        //操作是否成功的标志
        boolean flag=false;
        try{

        String insert = "insert into templatetype(typenum,typename) values(?,?)";
        pst=conn.prepareStatement(insert);
        pst.setObject(1,templateType.getType());
        pst.setObject(2, templateType.getTypeName());
        int n=pst.executeUpdate();
        this.pst.close();
        if(n>0) flag=true;
        }catch(Exception e){flag=false;}
        return flag;
    }

    public List<TemplateType> findAll()throws Exception
    {
        String select="select * from templatetype";
        //结果表
        List list=new ArrayList();
        this.pst=this.conn.prepareStatement(select);
        ResultSet rst=this.pst.executeQuery();
        while(rst.next())
        {
            TemplateType template=new TemplateType();
            template.setId(rst.getInt("id"));
            template.setType(rst.getInt("typenum"));
            template.setTypeName(rst.getString("typename"));
            list.add(template);
        }
        //当关闭pst的时候，认识他结果集也会随之关闭
        this.pst.close();

        return list;
    }

    //通过id查找用户
    @Override
    public TemplateType findByUid(int id)throws Exception
    {
        String sql="select * from templatetype where id=?";
        this.pst=this.conn.prepareStatement(sql);
        this.pst.setObject(1,id);
        ResultSet rst=this.pst.executeQuery();
        TemplateType template=null;
        if(rst.next())
        {
            template=new TemplateType();
            template.setId(rst.getInt("id"));
            template.setType(rst.getInt("typenum"));
            template.setTypeName(rst.getString("typename"));
        }
        this.pst.close();
        return template;
    }

    @Override
    public boolean update(TemplateType obj) {
        //操作是否成功的标志
        boolean flag=false;
        try{
            String insert = "update templatetype set typenum=?,typename=? where id=?";
            pst=conn.prepareStatement(insert);
            pst.setObject(1,obj.getType());
            pst.setObject(2, obj.getTypeName());
            pst.setObject(3, obj.getId());
            int n=pst.executeUpdate();
            pst.close();
            if(n>0) flag=true;
        }catch(Exception e){flag=false;e.printStackTrace();}
        return flag;
    }
}
