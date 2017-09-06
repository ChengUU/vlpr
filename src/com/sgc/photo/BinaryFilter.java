package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by ChengXX on 2017/3/18.
 */
public class BinaryFilter extends AbstractBufferedImageOp {
    public final static int MEAN_THRESHOLD=2;
    public final static int SHIFT_THRESHOLD=4;
    public final static int OSTU_THRESHOLD=6;

    private int thresholdType;
    private int threshold;

    public BinaryFilter(){this(MEAN_THRESHOLD);}
    public BinaryFilter(int thresholdType) {
        this.thresholdType = thresholdType;
    }

    public int getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(int thresholdType) {
        this.thresholdType = thresholdType;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        int width=src.getWidth();
        int height=src.getHeight();
        if(null==dest) dest=createCompatibleDestImage(src,null);
        int[] inPixles=new int[width*height];
        int[] outPixles=new int[width*height];
        getRGB(src,0,0,width,height,inPixles);
        Arrays.fill(outPixles,-16777216);
        int index=0;
        int mean=(int)getThreshhold(inPixles,width,height);
        threshold=mean;
        for(int row=0;row<height;row++){
            int ta=0,tr=0,tg=0,tb=0;
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixles[index]>>16)&0xff;
                if(tr>mean)
                    outPixles[index]=-1;
            }
        }
        setRGB(dest,0,0,width,height,outPixles);
        return dest;
    }

    public double getThreshhold(int[] pixles,int width,int height){
        int index=0;
        double mean=0;
        if(MEAN_THRESHOLD==thresholdType){
            int max=0;
            int min=255;
            double sum=0;
            for(int row=0;row<height;row++){
                int tr=0;
                for(int col=0;col<width;col++){
                    index=row*width+col;
                    tr=pixles[index];
                    min=Math.min(min,tr);
                    max=Math.max(max,tr);
                    sum+=tr;
                }
            }
            mean=sum/(width*height);
        }
        else if(SHIFT_THRESHOLD==thresholdType){
            mean=getMeanShiftThreshold(pixles,width,height);
        }else if(OSTU_THRESHOLD==thresholdType){
            mean=ostu(pixles,width,height);
        }
        return mean;
    }
    //通过一维MeanShift获取threshold
    private int getMeanShiftThreshold(int[] pixles,int width,int height){
        //maybe this value can reduce the calculation consume
        int initThreshold=127;
        int finalThreshold=0;
        int[] temp=new int[width*height];
        for(int index=0;index<temp.length;index++){
            temp[index]=(pixles[index]>>16)&0xff;
        }

        List<Integer> sub1=new ArrayList<>();
        List<Integer> sub2=new ArrayList<>();
        int mean1=0,mean2=0;
        while(finalThreshold!=initThreshold){
            finalThreshold=initThreshold;
            for(int i=0;i<temp.length;i++){
                if(temp[i]<=initThreshold) sub1.add(temp[i]);
                else sub2.add(temp[i]);
            }
            mean1=getMeans(sub1);
            mean2=getMeans(sub2);
            sub1.clear();
            sub2.clear();
            initThreshold=(mean1+mean2)/2;
        }

        System.out.println("finalThreshold="+finalThreshold);
        return finalThreshold;

    }

    private static int getMeans(List<Integer> data){
        int result=0;
        int size=data.size();
        if(0==size) return 0;
        for(Integer i:data){
            result+=i;
        }
        return result/size;
    }
    private int ostu(int[] pixles,int width,int height){
        int length=width*height;
        int L=256;
        int[] histogram=new int[L];
        Arrays.fill(histogram,0);
        // 统计各个灰度级出现次数
        for(int i=0;i<length;i++){
            int pixel=(pixles[i]>>16)&0xff;
            histogram[pixel]++;
        }
        double w0=0;
        double u0=0;
        double w1=0;
        double u1=0;
        double max=Double.MIN_VALUE;
        int mean=0;
        for(int t=0;t<L;t++){
            w0=u0=w1=u1=0;
            // 计算C0类
            for(int i=0;i<=t;i++){
               w1+=histogram[i];
               u1+=i*histogram[i];
            }
            u1=u1/w1;
            w1=w1/length;
            if(0==w1) break;
            // 计算C1类
            for(int i=t+1;i<L;i++){
                w0+=histogram[i];
                u0+=i*histogram[i];
            }
            if(0==w0) break;
            u0=u0/w0;
            w0=w0/length;
            // 计算最大类间方差
            double varValueI=w0*w1*(u0-u1)*(u0-u1);
            if(varValueI>max){
                max=varValueI;
                mean=t;
            }
        }
        return mean;
    }

    public double occupancy(BufferedImage image){
        double occ;
        setThresholdType(SHIFT_THRESHOLD);
        BufferedImage temp=filter(image,null);
        int width=temp.getWidth();
        int height=temp.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(temp,0,0,width,height,inPixels);
        int index,sum=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                if(-1==inPixels[index]) sum++;
            }
        }
        occ=sum*1.0/(width*height);
        return occ;
    }


    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
