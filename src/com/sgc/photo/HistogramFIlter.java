package com.sgc.photo;

import com.sgc.res.GrayClusSample;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/21.
 */
public class HistogramFIlter extends AbstractBufferedImageOp {
    private int[] getHistogram(int[] inPixel,int width,int height){
        int[] histogram=new int[256];
        int index=0;
        Arrays.fill(histogram,0);
        for(int row=0;row<height;row++){
            int tr=0;
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixel[index]>>16)&0xff;
                histogram[tr]++;
            }
        }
        return histogram;
    }

    public BufferedImage filter(BufferedImage sourceimage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceimage,null);
        int width=sourceimage.getWidth();
        int height= sourceimage.getHeight();
        int[] inPixel=new int[width*height];
        getRGB(sourceimage,0,0,width,height,inPixel);
        int[] histogram=getHistogram(inPixel,width,height);
        double maxFrequency=0;
        for(int i=0;i<histogram.length;i++){
            maxFrequency=Math.max(maxFrequency,histogram[i]);
        }
        // render the histogram graphic
        Graphics2D g=dest.createGraphics();
        g.setPaint(Color.LIGHT_GRAY);
        g.fillRect(0,0,width,height);
        // draw XY Axis
        g.setPaint(Color.BLACK);
        g.drawLine(20,50,20,height-50);
        g.drawLine(20,height-50,width-20,height-50);
        // draw XY title
        g.drawString("0",10,height-50);
        g.drawString(String.valueOf(maxFrequency),20,50);
        g.drawString("0",20,height-30);
        g.drawString("255",width-20,height-30);
        // draw histogram bar
        double xunit=(width-40)/256.0;
        double yunit=(height-100)/maxFrequency;
        for(int i=0;i<histogram.length;i++){
            double xp=20+xunit*i;
            double yp=yunit*histogram[i];
            Rectangle2D rect=new Rectangle2D.Double(xp,height-50-yp,xunit,yp);
            g.fill(rect);
        }
        return dest;
    }
    // this methos can not pass test
    public int[] getLinearGrayParameters(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixel=new int[width*height];
        getRGB(image,0,0,width,height,inPixel);
        int[] histogram=getHistogram(inPixel,width,height);
        int[] parameter=new int[2];
        Arrays.fill(parameter,0);
        int L=255;
        int N=width*height;
        int a=0,b=0;
        for(int i=0;i<=L;i++){
            if(10*a/N<N){
                a+=histogram[i];
                parameter[0]=i;
            }
            if(10*b/N<N){
                b+=histogram[L-i];
                parameter[1]=L-i;
            }
        }
        System.out.println(a+"-"+b);
        return parameter;
    }

    public GrayClusSample getSamples(BufferedImage image){
        GrayClusSample grayClusSample=new GrayClusSample();
        List<List<Double>> res=new ArrayList<>();
        // 图像灰度化处理
        GrayFilter grayFilter=new GrayFilter();
        BufferedImage temp=grayFilter.filter(image,null);
        // 获取灰度图像数据
        int width= temp.getWidth();
        int height=temp.getHeight();
        int[]inPixels=new int[width*height];
        getRGB(temp,0,0,width,height,inPixels);
        // 获取直方图统计数据
        int[] his=getHistogram(inPixels,width,height);
        // 直方图归一化
        double sum=0;
        Double[] hi=new Double[L];
        for(int i=0;i<L;i++) {
            sum+=his[i];
            List<Double> sample=new ArrayList<>();
            double h=i;
            sample.add(h);
            res.add(sample);
        }
        for(int i=0;i<L;i++) hi[i]=his[i]/sum;
        List<Double> weight=Arrays.asList(hi);
        grayClusSample.samples=res;
        grayClusSample.weight=weight;
        return grayClusSample;
    }

    public double jumpLine(BufferedImage image){
        double res=0;
        // 图像二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        BufferedImage temp=binaryFilter.filter(image,null);
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(temp,0,0,width,height,inPixels);
        boolean flag=true;
        int index,tr=0;
        double num=0,count=0;
        for(int row=0;row<height;row++){
            num=0;
            flag=true;
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=inPixels[index];
                if(!flag&&tr!=-1) flag=true;
                else if(flag&&tr==-1) {
                    flag=false;
                    num++;
                }
            }
            if(num>=7) count++;
        }
        if(count>0.2*height&&count<=0.85*height) res=2.2/(1+height/count*height/count);
        return res;
    }

    public static final int L=256;
}
