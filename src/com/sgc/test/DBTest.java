package com.sgc.test;

import com.sgc.bean.TemplateType;
import com.sgc.dao.TemplateTypeDaoProxy;

import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class DBTest {
    public static void main(String[] args)throws  Exception{
        TemplateTypeDaoProxy templateTypeDaoProxy=new TemplateTypeDaoProxy();
        TemplateType templateType=new TemplateType();
        templateType.setType(3);
        templateType.setTypeName("数字");
        templateTypeDaoProxy.doInsert(templateType);
        List<TemplateType> list=templateTypeDaoProxy.findAll();
        System.out.println(list);
     }
}
