package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/4/28.
 */
public class GrayEnergyCluster extends AbstractBufferedImageOp {
    public BufferedImage cluster(BufferedImage imgg,BufferedImage imgi){
        // 获取图像数据
        int width=imgg.getWidth();
        int height=imgg.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(imgg,0,0,width,height,inPixels);
        int[] inPixelsI=new int[width*height];
        getRGB(imgi,0,0,width,height,inPixelsI);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        // 获取AB值
        int[][] AB=compute(inPixelsI,width,height);
        int index,index2,ncol;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                if(-1!=inPixels[index]) continue;
                if(AB[0][index]>=0&&AB[1][index]!=0) output[index]=inPixelsI[index];
                else if(AB[0][index]<0||0==AB[1][index]){
                    ncol=col-2;
                    if(ncol<0) ncol=0;
                    index2=row*width+ncol;
                    output[index]=inPixelsI[index2];
                }
            }
        }
        // 求取对比度图像-从右到左,从上到下
        int ta,tr,tr1,tr2;
        for(int row=0;row<height;row++){
            for(int col=width-1;col>=0;col--){
                index=row*width+col;
                ncol=col-1;
                if(ncol<0) ncol=0;
                index2=row*width+ncol;
                ta=(output[index]>>24)&0xff;
                tr1=(output[index]>>16)&0xff;
                tr2=(output[index2]>>16)&0xff;
                tr=Math.abs(tr1-tr2);
                output[index]=(ta<<24)|(tr<<16)|(tr<<8)|tr;
            }
        }
        // 计算能量聚类图
        int[] clusRes=grayCluster(output,width,height);
        BufferedImage dest=createCompatibleDestImage(imgi,null);
        setRGB(dest,0,0,width,height,clusRes);
        return dest;
    }
    public int[] grayCluster(int[] inPixels,int width,int height){
        // 聚类结果
        int[] output=new int[width*height];
        // 计算能量聚类图
        //区域卷积半径
        double coe=1.0/(4.0*cRadius*rRadius);
        int nrow,ncol;
        int index,index2;
        int ta,tr;
        double trs;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                trs=tr=0;
                for(int subRow=-rRadius;subRow<=rRadius;subRow++){
                    nrow=row+subRow;
                    if(nrow<0||nrow>=height) nrow=row-subRow;
                    for(int subCol=-cRadius;subCol<=cRadius;subCol++){
                        ncol=col+subCol;
                        if(ncol<0||ncol>=width) ncol=col-subCol;
                        index2=nrow*width+ncol;
                        trs+=((inPixels[index2]>>16)&0xff);
                    }
                }
                trs=trs*coe;
                tr=(int)Math.floor(trs);
                output[index]=(ta<<24)|(tr<<16)|(tr<<8)|tr;
            }
        }
        return output;
    }
    public int[][] compute(int[] inPixels,int width,int height){
        int[][] AB=new int[2][width*height];
        int index,index2;
        int tr1,tr2;
        int ncol;
        for(int row=0;row<height;row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ncol = col - 2;
                if (ncol < 0) ncol = 0;
                index2 = row * width + col;
                tr1 = (inPixels[index] >> 16) & 0xff;
                tr2 = (inPixels[index2] >> 16) & 0xff;
                AB[0][index] = tr1 - tr2;
                ncol = col + 2;
                if (ncol >= width) ncol = width - 1;
                index2 = row * width + col;
                tr2 = (inPixels[index2] >> 16) & 0xff;
                AB[1][index] = tr1 - tr2;
                AB[0][index] = AB[0][index] * AB[1][index];
                AB[1][index] = AB[0][index] + AB[1][index];
            }
        }
        return AB;
    }
    public GrayEnergyCluster(){
        this(7,7);
    }

    public GrayEnergyCluster(int cRadius, int rRadius) {
        this.cRadius = cRadius;
        this.rRadius = rRadius;
    }

    private int cRadius=7;
    private int rRadius=7;
}
