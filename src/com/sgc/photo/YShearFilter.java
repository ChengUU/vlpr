package com.sgc.photo;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/31.
 */
public class YShearFilter extends AbstractBufferedImageOp{
    public YShearFilter(){
        this.backGround=Color.WHITE;
        this.angle=45;
    }
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        // convert degree to radian
        double radian=angle*DEGREE_TO_RADIAN;
        outw=width;
        outh=(int)(height+width*Math.tan(radian));
        // 图像数据获取
        int[] inPixels=new int[width*height];
        int[] outPixel=new int[outw*outh];
        getRGB(sourceImage,0,0,width,height,inPixels);
        int index=0;
        for(int row=0;row<outh;row++){
            int ta=255;
            for(int col=0;col<outw;col++){
                double pcol=col;
                double prow=row+Math.tan(radian)*(col-width);
                index=row*outw+col;
                int[] rgb=getPixel(inPixels,width,height,prow,pcol);
                outPixel[index]=(ta<<24)|(clamp(rgb[0])<<16)|(clamp(rgb[1])<<8)|clamp(rgb[2]);
            }
        }
        if(null==dest) dest=new BufferedImage(outw,outh,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,outw,outh,outPixel);
        return dest;

    }

    public static double getDegreeToRadian() {
        return DEGREE_TO_RADIAN;
    }

    public int getOutw() {
        return outw;
    }

    public void setOutw(int outw) {
        this.outw = outw;
    }

    public int getOuth() {
        return outh;
    }

    public void setOuth(int outh) {
        this.outh = outh;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Color getBackGround() {
        return backGround;
    }

    public void setBackGround(Color backGround) {
        this.backGround = backGround;
    }

    public static final double DEGREE_TO_RADIAN=3.1415926/180.0;
    private int outw;
    private int outh;
    private double angle;
    private Color backGround;
}
