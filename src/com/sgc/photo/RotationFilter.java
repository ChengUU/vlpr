package com.sgc.photo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/30.
 */
public class RotationFilter extends  AbstractBufferedImageOp {
    public static final double DEGREE_TO_RADIAN=3.1415926/180.0;
    private Color backGround=Color.BLACK;

    public BufferedImage rotate(BufferedImage image,double angle){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        // 获取图像数据
        getRGB(image,0,0,width,height,inPixels);
        // 计算弧度、
        double radian=angle*DEGREE_TO_RADIAN;
        // 原始图像四个角的坐标
        int sx1,sx2,sx3,sx4;
        int sy1,sy2,sy3,sy4;
        // 正弦余弦
        double cosine=Math.cos(radian);
        double sine=Math.sin(radian);
        // 左上角
        sx1=0;
        sy1=0;
        // 右上角
        sx2=width-1;
        sy2=0;
        // 左下角
        sx3=0;
        sy3=height-1;
        // 右下角
        sx4=width-1;
        sy4=height-1;
        // 旋转后图像四个角的坐标
        double nx1,nx2,nx3,nx4;
        double ny1,ny2,ny3,ny4;
        // 左上角
        nx1=sx1*cosine+sy1*sine;
        ny1=sy1*cosine-sx1*sine;
        // 右上角
        nx2=sx2*cosine+sy2*sine;
        ny2=sy2*cosine-sx2*sine;
        // 左下角
        nx3=sx3*cosine+sy3*sine;
        ny3=sy3*cosine-sx3*sine;
        // 右下角
        nx4=sx4*cosine+sy4*sine;
        ny4=sy4*cosine-sx4*sine;
        // 计算旋转后图像大小
        int newWidth=(int)Math.max(Math.abs(nx4-nx1),Math.abs(nx3-nx2));
        int newHeight=(int)Math.max(Math.abs(ny4-ny1),Math.abs(ny3-ny2));
        // 构造新图像数据存放空间
        int[] output=new int[newHeight*newWidth];
        // 初始化新图像数据
        Arrays.fill(output,255<<24);
        // 新图像对应的原始图像坐标
        double x,y;
        // 逆转换参数
        double invers_coe1=-0.5*newWidth*cosine+0.5*newHeight*sine+0.5*width;
        double invers_coe2=-0.5*newWidth*sine-0.5*newHeight*cosine+0.5*height;
        // 图像数据索引下标
        int new_index;
        int ta=255;
        for(int row=0;row<newHeight;row++){
            for(int col=0;col<newWidth;col++){
                y=row*cosine+col*sine+invers_coe2;
                x=col*cosine-row*sine+invers_coe1;
                new_index=row*newWidth+col;
                int[] rgb=getPixel(inPixels,width,height,y,x);
                output[new_index]=(ta<<24)|(clamp(rgb[0])<<16)|(clamp(rgb[1])<<8)|clamp(rgb[2]);
            }
        }
        BufferedImage dest=new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,newWidth,newHeight,output);
        return dest;
    }
    public BufferedImage shearTransform(BufferedImage image,double horAngle,double verAngle){
        // 正余弦值
        double sinea=Math.sin(horAngle*DEGREE_TO_RADIAN);
        double sineb=Math.sin(verAngle*DEGREE_TO_RADIAN);
        // 调整系数
        double coe=1.0/(1-sinea*sineb);
        // 图像尺寸
        int width=image.getWidth();
        int height=image.getHeight();
        // 图像数据
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);


        // 构造输出像素内存空间
        int[] output=new int[height*width];
        Arrays.fill(output,-1);
        double prow,pcol;
        int index=0;
        int ta=255;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                prow=row+col*sinea;
                pcol=col+row*sineb;
                int[] rgb=getPixel(inPixels,width,height,prow,pcol);
                index=row*width+col;
                output[index]=(ta<<24)|(clamp(rgb[0])<<16)|(clamp(rgb[1])<<8)|clamp(rgb[2]);
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;

    }
}
