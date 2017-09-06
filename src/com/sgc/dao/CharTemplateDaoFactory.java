package com.sgc.dao;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class CharTemplateDaoFactory {
    public static Dao getDaoInstance(){
        return new CharTemplateDaoProxy();
    }
}
