package com.sgc.dao;

import com.sgc.bean.CharTemplate;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/20.
 */
public interface CharTemplateDao extends Dao<CharTemplate>{
    public List<CharTemplate> queryByPath(String path) throws SQLException;
    public List<CharTemplate> queryByType(int type) throws SQLException;
}
