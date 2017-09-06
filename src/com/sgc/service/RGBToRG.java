package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;
import com.sgc.res.RGColorSpace;
import com.sgc.res.RGColorSpcSample;
import com.sgc.res.RGHis;
import com.sgc.segmentation.Cluster;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/24.
 */
public class RGBToRG extends AbstractBufferedImageOp {
    public static String RGHIS_OUTPUT_FILE="E:/gpro/Cluster/rghis_out.txt";
    public static RGColorSpace rgbToRG(BufferedImage image){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 构造转换结果
        RGColorSpace rgColorSpace=new RGColorSpace();
        rgColorSpace.width=width;
        rgColorSpace.height=height;
        int size=width*height;
        rgColorSpace.rg=new double[size][2];
        for(int i=0;i<size;i++) Arrays.fill(rgColorSpace.rg[i],0);
        // 转换过程需要的变量
        int index,tr,tg,tb;
        // RGB to RG
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixels[index]>>16)&0xff;
                tg=(inPixels[index]>>8)&0xff;
                tb=inPixels[index]&0xff;
                rgColorSpace.rg[index]=RGBToRG(tr,tg,tb);
//                System.out.println("r,g:"+rgColorSpace.rg[index][0]+" "+rgColorSpace.rg[index][1]);
            }
        }
        return rgColorSpace;

    }

    private static double[] RGBToRG(int r,int g,int b){
        double sum=r+g+b+3*DELTA;
        double nr=(r+DELTA)/sum;
        double ng=(g+DELTA)/sum;
        return new double[]{nr,ng};
    }

    public static RGHis getRGHis(RGColorSpace rgColorSpace,int w,int h){
        // rg色度空间数据
        int width=rgColorSpace.width;
        int height=rgColorSpace.height;
        double[][] rgs=rgColorSpace.rg;

        // 构造rg直方图对象
        RGHis rgHisRes=new RGHis();
        rgHisRes.width=w;
        rgHisRes.height=h;
        // 计算rg色度空间直方图
        rgHisRes.his=new int[h][w];
        // 初始化
        for(int i=0;i<h;i++) Arrays.fill(rgHisRes.his[i],0);
        int index;
        double r,g;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                r=rgs[index][0];
                g=rgs[index][1];
//                System.out.print("r="+r+"g="+g);
                int nRow=(int)(r*h);
                int nCol=(int)(g*w);
//                System.out.println("nRow="+nRow+"nCol="+nCol);
                rgHisRes.his[nRow][nCol]+=1;
            }
        }
        return rgHisRes;
    }

    public static RGColorSpcSample getSamples(RGHis rgHis){
        int width=rgHis.width;
        int height=rgHis.height;
        int[][] his=rgHis.his;
        // 构造样本对象
        RGColorSpcSample rgColorSpcSample=new RGColorSpcSample();
        rgColorSpcSample.samples=new ArrayList<>();
        rgColorSpcSample.p=new ArrayList<>();
        // 计算直方图样本点
        double x,y;
        int count=0;
        File result=new File(RGHIS_OUTPUT_FILE);
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter(result));
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
//                System.out.println("his:"+his[row][col]);
                    // 执行文件读写操作

//                    writer.write(String.format("%d ", his[row][col]));

                    if (his[row][col] > 50) {
                        List<Double> list = new ArrayList<>();
                        x = col;
                        y = row;
                        list.add(x);
                        list.add(y);
                        count += his[row][col];
                        rgColorSpcSample.samples.add(list);
//                        System.out.println("1");
                    }
                }
                writer.write("\r\n");
            }
            writer.close();
        }catch(IOException e) {
            System.out.println("数据打印出错......");
        }
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                if(his[row][col]>50){
                    double pi=1.0*his[row][col]/count;
//                    System.out.println("pi="+pi);
                    rgColorSpcSample.p.add(pi);
                }
            }
        }
        return  rgColorSpcSample;
    }

    public static final double DELTA=0.0000001;
}
