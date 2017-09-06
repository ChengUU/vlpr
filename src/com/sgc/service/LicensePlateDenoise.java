package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/4/28.
 */
public class LicensePlateDenoise extends AbstractBufferedImageOp {
    public static String RGHIS_OUTPUT_FILE="D:/ChengXX/Cluster/count_out.txt";
    public  BufferedImage preDenoise(BufferedImage image){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] M=new int[width*height];
        Arrays.fill(M,0);
        // 从左到右扫描
        int[] x=new int[5];
        int[] y=new int[5];
        int[] mVal=new int[5];
        int index,index2,index3,index4,index5,index6;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                if(-1!=inPixels[index]) continue;
                // 左上角
                x[0]=col-1;
                if(x[0]<0) x[0]=0;
                y[0]=row-1;
                if(y[0]<0) y[0]=0;
                index2=y[0]*width+x[0];
                mVal[0]=M[index2];
                // 正上方
                x[1]=col;
                y[1]=row-1;
                if(y[1]<0) y[1]=0;
                index3=y[1]*width+x[1];
                mVal[1]=M[index3];
                // 右上角
                x[2]=col+1;
                if(x[2]>=width) x[2]=width-1;
                y[2]=row-1;
                if(y[2]<0) y[2]=0;
                index4=y[2]*width+x[2];
                mVal[2]=M[index4];
                // 左侧点
                x[3]=col-1;
                if(x[3]<0) x[3]=0;
                y[3]=row;
                index5=y[3]*width+x[3];
                mVal[3]=M[index5];
                // 右侧点
                x[4]=col+1;
                if(x[4]>=width) x[4]=width-1;
                y[4]=row;
                index6=y[4]*width+x[4];
                mVal[4]=M[index6];
                if((-1==inPixels[index2])||(-1==inPixels[index3])||(-1==inPixels[index4])||(-1==inPixels[index5])||(-1==inPixels[index6])){
                    Arrays.sort(mVal);
                    M[index]=mVal[4]+1;
                }
            }
        }
        int[] N=new int[width*height];
        Arrays.fill(N,0);
        // 从右到左扫描
        for(int row=height-1;row>=0;row--){
            for(int col=width-1;col>=0;col--){
                index=row*width+col;
                if(-1!=inPixels[index]) continue;
                // 右下角
                x[0]=col+1;
                if(x[0]>=width) x[0]=width-1;
                y[0]=row+1;
                if(y[0]>=height) y[0]=height-1;
                index2=y[0]*width+x[0];
                mVal[0]=N[index2];
                // 正下方
                x[1]=col;
                y[1]=row+1;
                if(y[1]>=height) y[1]=height-1;
                index3=y[1]*width+x[1];
                mVal[1]=N[index3];
                // 左下角
                x[2]=col-1;
                if(x[2]<0) x[2]=0;
                y[2]=row+1;
                if(y[2]>=height) y[2]=height-1;
                index4=y[2]*width+x[2];
                mVal[2]=N[index4];
                // 右侧点
                x[3]=col+1;
                if(x[3]>=width) x[3]=width-1;
                y[3]=row;
                index5=y[3]*width+x[3];
                mVal[3]=N[index5];
                // 左侧点
                x[4]=col-1;
                if(x[4]<0) x[4]=0;
                y[4]=row;
                index6=y[4]*width+x[4];
                mVal[4]=N[index6];
                if((-1==inPixels[index2])||(-1==inPixels[index3])||(-1==inPixels[index4])||(-1==inPixels[index5])||(-1==inPixels[index5])){
                    Arrays.sort(mVal);
                    N[index]=mVal[4]+1;
                }
            }
        }
        File result=new File(RGHIS_OUTPUT_FILE);
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter(result));
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    index = row * width + col;
                    int count = M[index] + N[index];
                    writer.write(String.format("%d ", count));
//               System.out.print(String.format("%-4d",count));
                    if (count < 7 || count > 80) inPixels[index] = -16777216;
                }
                writer.write("\r\n");
//            System.out.println();
            }
        }catch(IOException e){}
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,inPixels);
        return dest;
    }

    public BufferedImage denoise(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        int index;
        int jBegin=0,jEnd=0,num=0;
        int row,col;
        for(row=0;row<height;row++){
            for(col=0;col<width;col++) {
                index = row * width + col;
                if (-1 != inPixels[index]) continue;
                if (col - jEnd >= TB && num >= T_CHANGES) {
                    copy(inPixels, output, row, jBegin, jEnd, width);
                    continue;
                } else if (col - jEnd >= TB && num < T_CHANGES) {
                    jBegin = col;
                    jEnd = col;
                    num = 0;
                    continue;
                }
                jEnd = col;
                num++;

            }
            if(col-jEnd>=TB&&num>=T_CHANGES) copy(inPixels,output,row,jBegin,jEnd,width);
            jBegin=jEnd=0;
            num=0;
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage denoiseConn(BufferedImage image){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 像素点位置
        int nrow,ncol;
        int index1,index2;
        // 扫描半径
        int cRadius=CONN_NOISE_WIDTH/2;
        int rRadius=CONN_NOISE_HEIGHT/2;
        // 计数变量
        int sum=0;
        // 扫描二值图像
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index1=row*width+col;
                if(-1!=inPixels[index1]) continue;
                sum=0;
                for(int subRow=-rRadius;subRow<=rRadius;subRow++){
                    nrow=row+subRow;
                    if(nrow<0||nrow>=height) nrow=row-subRow;
                    for(int subCol=-cRadius;subCol<=cRadius;subCol++){
                        ncol=col+subCol;
                        if(ncol<0||ncol>=width) ncol=col-subCol;
                        index2=nrow*width+ncol;
                        if(-1!=inPixels[index2]) continue;
                        sum++;
                    }
                }
                if(sum<cRadius*rRadius) inPixels[index1]=-16777216;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,inPixels);
        return dest;
    }

    public void copy(int[] src,int[] dest,int row,int col,int ncol,int width){
        int index;
            for(int j=col;j<=ncol;j++) {
                index = row * width + j;
                dest[index] = src[index];
            }
    }

    public final static  int CONN_NOISE_WIDTH=15;
    public final static  int CONN_NOISE_HEIGHT=15;

    public final static int TB=24;
    public final static int T_CHANGES=24;
}
