package com.sgc.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/8.
 */
public class Util {
    public static List<Double> copyList(List<Double> list){
        List<Double> res=new ArrayList<Double>();
        int size=list.size();
        for(int i=0;i<size;i++){
            double value=list.get(i);
            res.add(value);
        }
        return res;
    }
    public static List<List<Double>> copyLists(List<List<Double>> lists){
        List<List<Double>> res=new ArrayList<List<Double>>();
        for(List<Double> temp:lists){
            res.add(copyList(temp));
        }
        return res;
    }

    public static double euclidean_distance(List<Double> v1,List<Double> v2){
        int d=v1.size();
        double distance=0;
//        System.out.print("v1="+v1);
//        System.out.println("v2="+v2);
        for(int i=0;i<d;i++){
            distance+=(v1.get(i)-v2.get(i))*(v1.get(i)-v2.get(i));
        }
        return Math.sqrt(distance);
    }

    public static double  gaussian_kernel(double distance,double kernel_bandwidth){
        double temp=Math.exp(-0.5*(distance*distance)/(kernel_bandwidth*kernel_bandwidth));
        return 1.0/(kernel_bandwidth*Math.sqrt(2*Math.PI))*temp;
    }

    public static int[] grabber(Image img, int iw, int ih){
        int[] pix=new int[iw*ih];
        try{
            PixelGrabber pg=new PixelGrabber(img,0,0,iw,ih,pix,0,iw);
            pg.grabPixels();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return pix;
    }
    public static List<double[][]> waveletTransformHaar(double[][] X){
        List<double[][]> res=new ArrayList<double[][]>();
        int m,n,m2,n2,ik,ik1,jk,jk1;
        m=X.length;
        n=X[0].length;
        double[][] X0=new double[m+1][n+1];
        double a,b,c,d;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                X0[i][j]=X[i][j];
            }
        }
        for(int i=0;i<m+1;i++) X0[i][n]=X0[i][n-1];
        for(int j=0;j<n+1;j++) X0[m][j]=X0[m-1][j];
        m2=m/2;
        n2=n/2;
        if(2*m2<m) m2+=1;
        if(2*n2<n) n2+=1;

        double[][] LL=new double[m2][n2];
        double[][] LH=new double[m2][n2];
        double[][] HH=new double[m2][n2];
        double[][] HL=new double[m2][n2];
        ik=0;
        for(int i=0;i<m2;i++){
            ik1=ik+1;
            jk=0;
            for(int j=0;j<n2;j++){
                jk1=jk+1;
                a = X0[ik][jk];
                b = X0[ik][jk1];
                c = X0[ik1][jk];
                d = X0[ik1][jk1];
                LL[i][j] = (a + b + c + d) / 2;
                LH[i][j] = (a + b - c - d) / 2;
                HH[i][j] = (a - b + c - d) / 2;
                HL[i][j] = (a - b - c + d) / 2;

                jk += 2;
            }
            ik+=2;
        }
        res.add(HL);
        res.add(LH);
        res.add(HH);
        res.add(LL);
        return res;
    }

    public static float[] rgbToHSV(int[] rgb){
        float[] hsv=new float[3];
        Arrays.fill(hsv,0.0f);
        float min,max,delta;
        min=Math.min(rgb[0],Math.min(rgb[1],rgb[2]));
        max=Math.max(rgb[0],Math.max(rgb[1],rgb[2]));
        hsv[2]=max/255.0f;  // v
        delta=max-min;
        if(max!=0) hsv[1]=delta/max; //s
        else hsv[1]=0;
        // h
        if(0.0==delta) hsv[0]=0;
        else if(rgb[0]==max) hsv[0]=(rgb[1]-rgb[2])/delta;
        else if(rgb[1]==max) hsv[0]=2.0f+(rgb[2]-rgb[0])/delta;
        else hsv[0]=4+(rgb[0]-rgb[1])/delta;
        hsv[0]*=60;
        if(hsv[0]<0) hsv[0]+=360;
        return hsv;
    }

    public static int[] hsvToRGB(float[] hsv){
        int[] rgb=new int[3];
        Arrays.fill(rgb,0);
        int i;
        float f,p,q,t;
        if(0.0==hsv[1]) {
            rgb[0]=rgb[1]=rgb[2]=0;
            return rgb;
        }
        hsv[0]/=60.0;
        // 归一化处理
        hsv[2]*=255;
        i=(int)Math.floor(hsv[0]);
        f=hsv[0]-i;
        p=hsv[2]*(1-hsv[1]);
        q=hsv[2]*(1-hsv[1]*f);
        t=hsv[2]*(1-hsv[1]*(1-f));
        switch (i){
            case 0:rgb[0]=(int)hsv[2];rgb[1]=(int)t;rgb[2]=(int)p;break;
            case 1:rgb[0]=(int)q;rgb[1]=(int)hsv[2];rgb[2]=(int)p;break;
            case 2:rgb[0]=(int)p;rgb[1]=(int)hsv[2];rgb[2]=(int)t;break;
            case 3:rgb[0]=(int)p;rgb[1]=(int)q;rgb[2]=(int)hsv[2];break;
            case 4:rgb[0]=(int)t;rgb[1]=(int)p;rgb[2]=(int)hsv[2];break;
            case 5:rgb[0]=(int)hsv[2];rgb[1]=(int)p;rgb[2]=(int)q;break;
        }
        return rgb;
    }

    public static float[] rgbToHSV(int r,int g,int b){
        int[] rgb=new int[]{r,g,b};
        return rgbToHSV(rgb);
    }

    public static int[] hsvToRGB(float h,float s,float v){
        float[] hsv=new float[]{h,s,v};
        return hsvToRGB(hsv);
    }

    public static double computeVariance(List<Double> data){
        int size=data.size();
        // 求取平均值
        double sum=0,mean=0;
        for(int i=0;i<size;i++){
            sum+=data.get(i);
        }
        mean=sum/size;
        // 计算方差
        double delta=0;
        double count=0;
        for(int i=0;i<size;i++){
            delta=data.get(i)-mean;
            count+=delta*delta;
        }
        return 0!=size?count/size:0;
    }
    public static int[] markPeakAndTrough(int[] v){
        int[] oneDiff=diff(v);
        int trendLen=oneDiff.length;
        int[] trend=new int[trendLen];
        for(int i=0;i<trendLen;i++) {
            if(oneDiff[i]>0) trend[i]=1;
            else if(oneDiff[i]<0) trend[i]=-1;
            else trend[i]=0;
        }
        // 遍历trend
        for(int i=trendLen-1;i>=0;i--){
            if(trend[i]==0&&i==trendLen-1) trend[i]=1;
            else if(trend[i]==0){
                if(trend[i+1]>=0) trend[i]=1;
                else trend[i]=-1;
            }
        }

        // 进行一阶差分运算
        return diff(trend);

    }

    public static  int[] diff(int[] v){
        int len=v.length;
        int resLen=len-1;
        int[] res=new int[resLen];
        int i=0;
        for(i=0;i<resLen;i++){
            res[i]=v[i+1]-v[i];
        }
       return res;
    }

    public static void setFileTypeFilter(JFileChooser chooser){
        FileNameExtensionFilter filter=new FileNameExtensionFilter("JPG &PNG Images","jpg","png");
        chooser.setFileFilter(filter);
    }

    public static double computeGamaD(double value,double gama){
        return 255*Math.pow(value/255,1/gama);
    }
    public static double computeGamaB(double value,double gama){
        return 255*(1-Math.pow(1-value/255,1/gama));
    }
}
