package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/26.
 */
public class GrayErosinFilter extends ErosinFilter {
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();

        int[] inPixels=new int[width*height];
        int[] output=new int[width*height];
        // 获取图像数据
        getRGB(sourceImage,0,0,width,height,inPixels);

        // 结构元素相关参数
        int[][] se=getStructureElements();
        int se_width=se[0].length;
        int se_height=se.length;

        // 膨胀扫描半径
        int hr=se_height/2;
        int wr=se_width/2;
        // 坐标信息
        // 当前扫描点
        int curr_index;
        // 结构元素对应图像元素
        int index;
        // 已知最大值下标
        int min_index;

        // 当前计算结果
        int res;

        // 元素结构计算坐标以及图像元素对应坐标
        int x,y,x2,y2;

        // 当前图像数据像素大小
        int pa;

        int min=Integer.MAX_VALUE;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                curr_index=row*width+col;
                min_index=row*width+col;
                min=Integer.MAX_VALUE;
                for(int i=-hr;i<=hr;i++){
                    y=row+i;
                    if(y<0||y>=height) continue;
                    y2=hr+i;
                    for(int j=-wr;j<=wr;j++){
                        x=col+j;
                        if(x<0||x>=width) continue;
                        x2=wr+j;
                        index=y*width+x;
                        pa=(inPixels[index]>>16)&0xff;
                        res=pa-se[y2][x2];
                        if(res<min){
                            min=res;
                            min_index=index;
                        }
                    }
                }
                output[curr_index]=inPixels[min_index];
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
}
