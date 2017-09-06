package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;
import com.sgc.photo.RadonFilter;
import com.sgc.photo.RotationFilter;
import com.sgc.photo.ZoomFilter;
import com.sgc.res.ExcisionUnit;
import com.sgc.res.RadonRes;

import java.awt.image.BufferedImage;
import java.sql.BatchUpdateException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/19.
 */
public class NormalizationFactory extends AbstractBufferedImageOp{
    public static int CHARACTER_WIDTH=45;
    public  static int CHARACTER_HEIGHT=90;

    public static List<ExcisionUnit> normalize(List<ExcisionUnit> excisions){
        int width,height;
        // 获取分割字符迭代器
        Iterator<ExcisionUnit> ite=excisions.iterator();
        ZoomFilter zoomFilter=new ZoomFilter();
        int index=0;
        while(ite.hasNext()){
            ExcisionUnit excisionUnit=ite.next();
            if(excisionUnit==null) continue;
            width=excisionUnit.getWidth();
            height=excisionUnit.getHeight();
            BufferedImage temp=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            AbstractBufferedImageOp.setRGB(temp,0,0,width,height,excisionUnit.getPixels());
            if(width<CHARACTER_WIDTH){// 将图像置于中央
                BufferedImage imgA=new BufferedImage(CHARACTER_WIDTH,CHARACTER_HEIGHT,BufferedImage.TYPE_INT_RGB);
                temp=zoomFilter.centerImage(imgA,temp);
            }
            if(width>CHARACTER_WIDTH){//去除左右两边的空白区域
                temp=dealBlank(temp);
            }
            // 统一尺寸大小
            zoomFilter.setNewWidth(CHARACTER_WIDTH);
            zoomFilter.setNewHeight(CHARACTER_HEIGHT);

            temp=zoomFilter.zoom(temp);
            int[] output=new int[CHARACTER_HEIGHT*CHARACTER_WIDTH];
            getRGB(temp,0,0,CHARACTER_WIDTH,CHARACTER_HEIGHT,output);
            excisionUnit.setWidth(CHARACTER_WIDTH);
            excisionUnit.setHeight(CHARACTER_HEIGHT);
            excisionUnit.setPixels(output);
            excisionUnit.setIndex(index);
            index++;
        }
        return excisions;
    }

    public static BufferedImage dealBlank(BufferedImage image){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 获取投影数据
        int[][] hv=Projection.getProjectionData(inPixels,width,height);
        // 扫描垂直投影-从左到右,从右到左
        int left=-1,right=width;
        for(int col=0;col<width;col++) {
            if(0==hv[1][col]) continue;
            left=col;
            break;
        }
        for(int col=width-1;col>=0;col--) {
            if(0==hv[1][col]) continue;
            right=col;
            break;
        }
        int outw=right-left+1;
        if(width!=outw){
            int[] output=new int[outw*height];
            getRGB(image,left,0,outw,height,output);
            image=new BufferedImage(outw,height, BufferedImage.TYPE_INT_RGB);
            setRGB(image,0,0,outw,height,output);
        }
        return image;
    }
}
