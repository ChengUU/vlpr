package com.sgc.dao;

import com.sgc.bean.Pool;
import com.sgc.bean.TemplateType;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
    public class TemplateTypeDaoProxy implements TemplateTypeDao{
        private Pool pool=Pool.getInstance();
        private TemplateTypeDaoImpl dao=null;
        private Connection dbConn=null;

        public TemplateTypeDaoProxy()
        {
            this.dbConn= pool.getConnection();
            this.dao=new TemplateTypeDaoImpl(this.dbConn);
        }

        @Override
        public boolean doInsert(TemplateType user)
        {
            boolean flag=false;
            try{
                //判断该用户是否存在
                if(null==this.dao.findByUid(user.getId())){
                    //保存插入操作是否成功
                    flag=this.dao.doInsert(user);
                }
            }catch(Exception e){e.printStackTrace();}
            finally{
                pool.freeConnection(dbConn);
            }
            return flag;
        }
        public List<TemplateType> findAll()
        {
            List<TemplateType> list=null;
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
        public boolean update(TemplateType obj) {return  dao.update(obj);}

        @Override
        public TemplateType findByUid(int uid)
        {
            TemplateType user=null;
            try{
                user=dao.findByUid(uid);
            }catch(Exception e){e.printStackTrace();}
            return user;
        }

        @Override
        public boolean delete(int id) {
            return dao.delete(id);
        }
    }


