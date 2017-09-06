package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;
import com.sgc.photo.BinaryFilter;
import com.sgc.util.Util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/25.
 */
public class Projection extends AbstractBufferedImageOp{
    public BufferedImage getVerticalProjection(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(image,0,0,width,height,inPixels);
        int[] v=getProjectionData(inPixels,width,height)[1];
        int index=0;

        for(int col=0;col<width;col++){
            for(int row=0;row<v[col];row++){
                index=row*width+col;
                output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage getHorizontalProjection(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(image,0,0,width,height,inPixels);
        int[] v=getProjectionData(inPixels,width,height)[0];
        int index=0;

        for(int row=0;row<height;row++){
            for(int col=0;col<v[row];col++){
                index=row*width+col;
                output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    public int detjumpPoint(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 垂直投影字符检测跳变
        int[] v=getProjectionData(inPixels,width,height)[1];
        boolean flag=true;
        int count=0;
        // 设定投影阀值
        int threshold=height*10/100;
        for(int i=0;i<width;i++){
            //如果当前已经经历过谷底值，但仍旧未扫描到指定阀值
            if(!flag&&v[i]<=threshold) flag=true;
            else if(flag&&v[i]>threshold){
                flag=false;
                count++;
            }
        }
        return count;
    }

    // 扫描图像中间行获取二值跳变次数
    public int countBinaryPoint(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        boolean flag=true;
        int count=0;
        // 中间行
        int row=(height+1)/2;
        // 设定跳变阀值
        int threshold=255;
        // 中间行像素坐标及该点的像素值
        int index;
        int pixel;
        for(int i=0;i<width;i++){
            index=row*width+i;
            pixel=(inPixels[index]>>16)&0xff;
            if(!flag&&pixel==threshold) flag=true;
            else if(flag&&pixel!=threshold){
                flag=false;
                count++;
            }
        }
        return count;
    }

    // 灰度图像数据
    public static int[][] getProjectionData(int[] inPixels,int width,int height){
        int[] v=new int[width];
        int[] h=new int[height];
        Arrays.fill(v,0);
        Arrays.fill(h,0);
        int tr=0;
        int index=0;
        // 统计对应列的黑点的个数
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixels[index]>>16)&0xff;
                if(tr>127){
                    v[col]++;
                    h[row]++;
                }
            }
        }
        return new int[][]{h,v};
    }
    // 参数为灰度图像
    public double troughJumpCount(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        BufferedImage temp=binaryFilter.filter(image,null);
        getRGB(temp,0,0,width,height,inPixels);
       int[][] hv=getProjectionData(inPixels,width,height);
       int[] trgPeak=Util.markPeakAndTrough(hv[1]);
       double count=0;
       for(int col=0;col+2<width;col++){
           if(-2==trgPeak[col]) count++;
       }
       return count;
    }

    public int countTrough(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 垂直投影字符检测跳变
        int[][] v=getProjectionData(inPixels,width,height);
        // 计算波峰波谷
        int[] troughAndPeak= Util.markPeakAndTrough(v[1]);
        int trgCount=0;
        int peakCount=0;
        int len=troughAndPeak.length;
        for(int i=0;i<len;i++){
            if(-2==troughAndPeak[i]) peakCount++;
            if(2==troughAndPeak[i]) trgCount++;
        }
        return (peakCount+trgCount)/2;
    }
}
