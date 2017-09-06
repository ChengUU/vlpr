package com.sgc.service;

/**
 * Created by ChengXX on 2017/4/12.
 */
public class WhiteColorPicker extends ColorPicker {
    public WhiteColorPicker(float hMin, float hMax, float sMin, float sMax, float vMin, float vMax) {
        super(hMin, hMax, sMin, sMax, vMin, vMax);
    }

    public boolean check(float[] hsv){
        boolean flag=true;
        if(hsv[2]<vMin||hsv[2]>vMax) flag=false;
        return flag;
    }
}
