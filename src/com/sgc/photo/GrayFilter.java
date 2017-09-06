package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

/**
 * Created by ChengXX on 2017/3/14.
 */
public class GrayFilter extends AbstractBufferedImageOp {
    private double mean;
    public BufferedImage filter(BufferedImage src,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(src,null);
        int width=src.getWidth();
        int height=src.getHeight();
        int[] inPixles=new int[width*height];
        int[] outpixles=new int[width*height];
        getRGB(src,0,0,width,height,inPixles);
        int index=0;
        int ta=0,tr=0,tg=0,tb=0;
        int sum=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixles[index]>>24)&0xff;
                tr=(inPixles[index]>>16)&0xff;
                tg=(inPixles[index]>>8)&0xff;
                tb=inPixles[index]&0xff;
                int gray=(int)(0.299*tr+0.587*tg+0.114*tb);
                sum+=gray;
                outpixles[index]=(ta<<24)|(gray<<16)|(gray<<8)|gray;
            }
        }
        mean=sum/(width*height);
        setRGB(dest,0,0,width,height,outpixles);
        return dest;
    }
    // 灰度图像灰度方差
    public double computeVariance(BufferedImage image){
        double res=0;
        BufferedImage temp=filter(image,null);
        int width=temp.getWidth();
        int height=temp.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(temp,0,0,width,height,inPixels);
        double sum=0;
        int index,tr;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixels[index]>>16)&0xff;
                sum+=(tr-mean);
            }
        }
        System.out.println("sum="+sum);
        res=sum/(width*height);
        System.out.println(res);
        return res;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }
}
