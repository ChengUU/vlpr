package com.sgc.res;

/**
 * Created by ChengXX on 2017/4/8.
 */
public class ExcisionUnit  {
    private int index;
    private int[] pixels;
    private int width;
    private int height;

    public ExcisionUnit(int i, int width, int height, int[] pixels) {
        this.index=i;
        this.width=width;
        this.height=height;
        this.pixels=pixels;

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
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
}
