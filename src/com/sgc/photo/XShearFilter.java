package com.sgc.photo;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/31.
 */
public class XShearFilter extends AbstractBufferedImageOp {
    public static final double DEGREE_TO_RADIAN=3.1415926/180.0;
    private int outw;
    private int outh;
    private double angle;
    private Color backGround;

    public XShearFilter(){
        this.backGround=Color.BLACK;
        this.angle=45;
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

    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        // convert degree to radian
        double radian=angle*DEGREE_TO_RADIAN;
        // 目标图像的尺寸
        outh=height;
        // 竖直方向顺时针夹角
        outw=(int)(width+height*Math.tan(radian));

        // 获取图像数据
        int[] inPixels=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        // 输出图像内存空间
        int[] outPixels=new int[outw*outh];
        int index=0;
        for(int row=0;row<outh;row++){
            int ta=255;
            for(int col=0;col<outw;col++){
                double prow=row;
                double pcol=col+Math.tan(radian)*(row-height);
                int[] rgb=getPixel(inPixels,width,height,prow,pcol);
                index=row*outw+col;
                outPixels[index]=(ta<<24)|(clamp(rgb[0])<<16)|(clamp(rgb[1])<<8)|clamp(rgb[2]);
            }
        }
        if(null==dest) dest=new BufferedImage(outw,outh,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,outw,outh,outPixels);
        return dest;
    }
}
