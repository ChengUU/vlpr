package com.sgc.JComPz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import com.sgc.dao.Dao;
import com.sgc.bean.TemplateType;
import com.sgc.dao.TemplateTypeFactory;

public class MapPz {
	static Map map = new HashMap();

	public static Map getMap() {
		Dao<TemplateType> dao=TemplateTypeFactory.getDaoInstance();
		try {
			List<TemplateType> list = dao.findAll();
			for (int i = 0; i < list.size(); i++) {
				TemplateType booktype = list.get(i);

				Item item = new Item();
				item.setId(booktype.getType());
				item.setName(booktype.getTypeName());
				map.put(item.getId(), item);

			}
		}catch(Exception e){e.printStackTrace();}
		return map;
	}
}
