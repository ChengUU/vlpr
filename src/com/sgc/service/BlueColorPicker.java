package com.sgc.service;

/**
 * Created by ChengXX on 2017/4/12.
 */
public class BlueColorPicker extends ColorPicker {
    public BlueColorPicker(float hMin, float hMax, float sMin, float sMax, float vMin, float vMax) {
        super(hMin, hMax, sMin, sMax, vMin, vMax);
    }

    public boolean check(float[] hsv){
        boolean flag=true;
        if(hsv[0]<hMin||hsv[0]>hMax) flag=false;
        if(hsv[1]<sMin||hsv[1]>sMax) flag=false;
        return flag;
    }
}
