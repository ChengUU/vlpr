package com.sgc.test;

import com.sgc.util.Util;

/**
 * Created by ChengXX on 2017/4/12.
 */
public class RGBToHSVTest {
    public static void main(String[] args){
        int r=255,g=255,b=255;
        float[] hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=255;
        g=0;
        b=0;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=0;
        g=255;
        b=0;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=0;
        g=0;
        b=255;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=0;
        g=2;
        b=3;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=10;
        g=10;
        b=10;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=55;
        g=128;
        b=44;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        r=255;
        g=255;
        b=0;
        hsv= Util.rgbToHSV(r,g,b);
        System.out.println(String.format("H:%.2f S:%.2f V:%.2f",hsv[0],hsv[1],hsv[2]));
        float h=55f,s=0.4f,v=0.4f;
        int[] rgb=Util.hsvToRGB(h,s,v);
        System.out.println(String.format("R:%d G:%d B:%d",rgb[0],rgb[1],rgb[2]));
    }
}
