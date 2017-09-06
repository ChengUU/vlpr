package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/4/1.
 */
public class ZoomFilter extends AbstractBufferedImageOp{
    private int newWidth;
    private int newHeight;
    public ZoomFilter(){
        this(256,256);
    }

    public ZoomFilter(int newWidth,int newHeight){
        this.newHeight=newHeight;
        this.newWidth=newWidth;
    }

    public int getNewWidth() {
        return newWidth;
    }

    public void setNewWidth(int newWidth) {
        this.newWidth = newWidth;
    }

    public int getNewHeight() {
        return newHeight;
    }

    public void setNewHeight(int newHeight) {
        this.newHeight = newHeight;
    }

    //采用双线性插值缩放图片
    public BufferedImage zoom(BufferedImage image){
        // 坐标变换公式 a=x*width/DstWidth b=y*height/DstHeight
        int width=image.getWidth();
        int height=image.getHeight();
        // 坐标变换参数
        double ver_coe=1.0*width/newWidth;
        double hor_coe=1.0*height/newHeight;
        // 原图数据
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 缩放后图像数据
        int[] outPixels=new int[newHeight*newWidth];
        for(int row=0;row<newHeight;row++){
            for(int col=0;col<newWidth;col++){
                double prow=row*hor_coe;
                double pcol=col*ver_coe;
                int[] rgb=getPixel(inPixels,width,height,prow,pcol);
                int index=row*newWidth+col;
                outPixels[index]=(255<<24)|(clamp(rgb[0])<<16)|(clamp(rgb[1])<<8)|clamp(rgb[2]);
            }
        }
        //保存缩放图像数据
        BufferedImage dest=new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,newWidth,newHeight,outPixels);
        return dest;
    }
    // 指定宽度,按照原图比例缩放
    public BufferedImage zoom(BufferedImage image,int newWidth){
        int width=image.getWidth();
        int height=image.getHeight();
        double rate=1.0*newWidth/width;
        this.newHeight=(int)(height*rate+0.5);
        this.newWidth=newWidth;
        return zoom(image);
    }

    // 将小图像放置在大图像的中央
    public BufferedImage centerImage(BufferedImage imgA,BufferedImage imgB){
       //图片A尺寸
        int ha=imgA.getHeight();
        int wa=imgA.getWidth();
        int[] output=new int[wa*ha];
        //图片B尺寸
        int hb=imgB.getHeight();
        int wb=imgB.getWidth();
        int[] inPixels=new int[wb*hb];
        getRGB(imgB,0,0,wb,hb,inPixels);
        if(ha!=hb) throw new IllegalArgumentException("As high as two pictures.");
        // 计算居中左右边界
        int left=wa/2-wb/2;
        int right=left+wb;
        // 坐标值
        int index1,index2;
        // 实现居中
        for(int row=0;row<hb;row++){
            for(int col=0;col<wb;col++){
                // 图像A对应坐标点
                index1=row*wa+left+col;
                // 图像B坐标点
                index2=row*wb+col;
                output[index1]=inPixels[index2];
            }
        }
        setRGB(imgA,0,0,wa,ha,output);
        return imgA;
    }
}
