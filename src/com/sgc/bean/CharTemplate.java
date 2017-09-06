package com.sgc.bean;

/**
 * Created by ChengXX on 2017/4/20.
 */
public class CharTemplate {
    private int id;
    private int typeId;
    private int width;
    private int height;
    private String path;

    public CharTemplate(){}

    public CharTemplate(int id, int typeId, int width, int height, String path, String repChar) {
        this.id = id;
        this.typeId = typeId;
        this.width = width;
        this.height = height;
        this.path = path;
        this.repChar = repChar;
    }

    public CharTemplate(int typeId, int width, int height, String path, String repChar) {
        this.typeId = typeId;
        this.width = width;
        this.height = height;
        this.path = path;
        this.repChar = repChar;
    }

    private String repChar;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRepChar() {
        return repChar;
    }

    public void setRepChar(String repChar) {
        this.repChar = repChar;
    }
}
