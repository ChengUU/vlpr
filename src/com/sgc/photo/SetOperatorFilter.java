package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class SetOperatorFilter extends AbstractBufferedImageOp{
    // 集合操作:并
    private final static int UNION=1;
    // 集合操作:交
    public final static int INTERSECTION=2;
    // 集合操作:补
    public final static int COMPLEMENT=3;
    // 集合操作:差
    public final static int DIFFERENCE=4;

    // 集合操作
    private int operatorType;

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    // 集合操作:并
    public BufferedImage union(BufferedImage A, BufferedImage B){
        // 获取图像宽和高
        int width=A.getWidth();
        int height=A.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==B||width!=B.getWidth()||height!=B.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(A,0,0,width,height,setA);
        getRGB(B,0,0,width,height,setB);
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int g1=getPixel(setA,width,height,row,col);
                int g2=getPixel(setB,width,height,row,col);
                if(g1<127&&g2<127) continue;
                output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(A,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    // 集合操作:补
    public BufferedImage complement(BufferedImage A){
        // 获取图像宽和高
        int width=A.getWidth();
        int height=A.getWidth();
        int[] setA=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(A,0,0,width,height,setA);
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int g1=getPixel(setA,width,height,row,col);
                if(g1>127)
                output[index]=-16777216;
                else
                    output[index]=(0xff<<24)|(200<<16)|(200<<8)|200;
            }
        }
        BufferedImage dest=createCompatibleDestImage(A,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    // 集合操作:差
    public BufferedImage difference(BufferedImage A, BufferedImage B){
        // 获取图像宽和高
        int width=A.getWidth();
        int height=A.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==B||width!=B.getWidth()||height!=B.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(A,0,0,width,height,setA);
        getRGB(B,0,0,width,height,setB);
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int g1=getPixel(setA,width,height,row,col);
                int g2=getPixel(setB,width,height,row,col);
                //求补集
                if(g2>127) g2=0;
                else g2=255;
                if(g1>127&&g2>127) output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(A,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    // 集合操作:交
    public BufferedImage intersection(BufferedImage A, BufferedImage B){
        // 获取图像宽和高
        int width=A.getWidth();
        int height=A.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==B||width!=B.getWidth()||height!=B.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(A,0,0,width,height,setA);
        getRGB(B,0,0,width,height,setB);
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int g1=getPixel(setA,width,height,row,col);
                int g2=getPixel(setB,width,height,row,col);
                if(g1>127&&g2>127) output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(A,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }



    private int getPixel(int[] input,int width,int height,int row,int col){
        if(col<0||col>=width) col=0;
        if(row<0||row>=height) row=0;
        int index=row*width+col;
        int tr=(input[index]>>16)&0xff;
        return tr;
    }
}
