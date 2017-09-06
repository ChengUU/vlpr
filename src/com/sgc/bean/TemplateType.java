package com.sgc.bean;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class TemplateType {
    private int id;
    private int typeNum;
    private String typeName;

    public TemplateType() {}

    public TemplateType(int type, String typeName){this(0,type,typeName);}

    public TemplateType(int id,int type, String typeName) {
        this.id=id;
        this.typeNum = type;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return typeNum;
    }

    public void setType(int type) {
        this.typeNum = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TemplateType{" +
                "id=" + id +
                ", type=" + typeNum +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
