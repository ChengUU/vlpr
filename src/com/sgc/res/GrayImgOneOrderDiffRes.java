package com.sgc.res;

/**
 * Created by ChengXX on 2017/4/1.
 */
public class GrayImgOneOrderDiffRes {
    private final int width;
    private final int height;
    private final int[] oneOrderDiff;

    public GrayImgOneOrderDiffRes(int width, int height, int[] oneOrderDiff) {
        this.width = width;
        this.height = height;
        this.oneOrderDiff = oneOrderDiff;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getOneOrderDiff() {
        return oneOrderDiff;
    }
}
