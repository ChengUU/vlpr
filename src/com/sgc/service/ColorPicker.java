package com.sgc.service;

/**
 * Created by ChengXX on 2017/4/12.
 */
public abstract class ColorPicker {
    protected final float hMin;
    protected final float hMax;
    protected final float sMin;
    protected final float sMax;
    protected final float vMin;
    protected final float vMax;
    public ColorPicker(float hMin, float hMax, float sMin, float sMax, float vMin, float vMax) {
        this.hMin = hMin;
        this.hMax = hMax;
        this.sMin = sMin;
        this.sMax = sMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public abstract boolean check(float[] hsv);
}
