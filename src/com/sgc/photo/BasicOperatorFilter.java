package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class BasicOperatorFilter extends  AbstractBufferedImageOp {
    // 减法运算
    public BufferedImage minus(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int ta=0;
        int ra,rb;
        int ga,gb;
        int ba,bb;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(setA[index]>>24)&0xff;
                ra=(setA[index]>>16)&0xff;
                ga=(setA[index]>>8)&0xff;
                ba=setA[index]&0xff;

                rb=(setB[index]>>16)&0xff;
                gb=(setB[index]>>8)&0xff;
                bb=setB[index]&0xff;

                ra=clamp(ra-rb);
                ga=clamp(ga-gb);
                ba=clamp(ba-bb);

                output[index]=(ta<<24)|(ra<<16)|(ga<<8)|ba;
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    public BufferedImage add(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int ta=0;
        int ra,rb;
        int ga,gb;
        int ba,bb;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(setA[index]>>24)&0xff;
                ra=(setA[index]>>16)&0xff;
                ga=(setA[index]>>8)&0xff;
                ba=setA[index]&0xff;

                rb=(setB[index]>>16)&0xff;
                gb=(setB[index]>>8)&0xff;
                bb=setB[index]&0xff;

                ra=clamp(ra+rb);
                ga=clamp(ga+gb);
                ba=clamp(ba+bb);

                output[index]=(ta<<24)|(ra<<16)|(ga<<8)|ba;
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage scale(BufferedImage imgA,double scale){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        int[] setA=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        int ta=0;
        int ra;
        int ga;
        int ba;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(setA[index]>>24)&0xff;
                ra=(setA[index]>>16)&0xff;
                ga=(setA[index]>>8)&0xff;
                ba=setA[index]&0xff;


                ra=clamp((int)(ra*scale));
                ga=clamp((int)(ga*scale));
                ba=clamp((int)(ba*scale));

                output[index]=(ta<<24)|(ra<<16)|(ga<<8)|ba;
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    public BufferedImage max(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int ta=0;
        int ra,rb;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ra=(setA[index]>>16)&0xff;
                rb=(setB[index]>>16)&0xff;
                if(ra>rb)
                    output[index]=setA[index];
                else{
                    output[index]=setB[index];
                }
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage min(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int ta=0;
        int ra,rb;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ra=(setA[index]>>16)&0xff;
                rb=(setB[index]>>16)&0xff;
                if(ra<rb)
                    output[index]=setA[index];
                else{
                    output[index]=setB[index];
                }
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage normal(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] setA=new int[width*height];
        int[] output=new int[width*height];
        getRGB(image,0,0,width,height,setA);
        int ta=0;
        int ra;
        int ga;
        int ba;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(setA[index]>>24)&0xff;
                ra=(setA[index]>>16)&0xff;


                if(ra>127)  ra=ga=ba=255;
                else ra=ga=ba=0;

                output[index]=(ta<<24)|(ra<<16)|(ga<<8)|ba;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    // 与运算
    public BufferedImage and(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int a,b;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                a=setA[index];
                b=setB[index];
                if(-1==a&&a==b) output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    // 或运算
    public BufferedImage or(BufferedImage imgA,BufferedImage imgB){
        int width=imgA.getWidth();
        int height=imgA.getHeight();
        // 判断图像B是否为空以及与图像A尺寸大小是否一致
        if(null==imgB||width!=imgB.getWidth()||height!=imgB.getHeight()) throw new IllegalArgumentException("width and height must be same between image A and B");
        int[] setA=new int[width*height];
        int[] setB=new int[width*height];
        int[] output=new int[width*height];
        getRGB(imgA,0,0,width,height,setA);
        getRGB(imgB,0,0,width,height,setB);
        int a,b;
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                a=setA[index];
                b=setB[index];
                if(-1==a||-1==b) output[index]=-1;
            }
        }
        BufferedImage dest=createCompatibleDestImage(imgA,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
}
