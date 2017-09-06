package com.sgc.photo;

import com.sgc.test.DWTTest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/9.
 */
public class TEST {
    private int M;
    private int N;
    private int width;
    private int height;
    private int minx;
    private int miny;
    private int[] pixles;
    BufferedImage srcImage;
    private List<List<Double>> samples=new ArrayList<>();
    private List<List<Double>> rgbs=new ArrayList<>();

    public TEST(BufferedImage srcImage)throws IOException{
        this.srcImage= srcImage;
        this.width = srcImage.getWidth();
        this.height = srcImage.getHeight();
        this.minx = srcImage.getMinX();
        this.miny = srcImage.getMinY();
        this.M=this.width-this.minx;
        this.N=this.height-this.miny;
        this.pixles=new int[M*N];
    }
    public int getM() {
        return M;
    }


    public int getN() {
        return N;
    }


    public List<List<Double>> getSamples(){
        if(this.samples.size()>0) return this.samples;
        double[] rgb=new double[3];
        double[] tempRGB=new double[3];
        double[][] harrs=new double[BLOCK][BLOCK];
        double Xi,Yi,L[]=new double[3],T[]=new double[3];
        int xi,yi;
        List<Double> temp=new ArrayList<>();
        List<Double> Lab=new ArrayList<>();
        for(int z=0;z<D;z++){
            temp.add(0.0);
            Lab.add(0.0);
        }
        for (int i = miny; i < height; i+=BLOCK) {
            for (int j = minx; j < width; j+=BLOCK) {
                //原始像素行
                int x=i;
                //原始像素列
                int y=j;
                for(int z=0;z<D;z++){
                    temp.set(z,0.0);
                    Lab.set(z,0.0);
                }
                for(int z=0;z<3;z++){
                    L[z]=0;
                    T[z]=0;
                }
                Xi=0;
                Yi=0;
                Arrays.fill(tempRGB,0.0);
                for(int k=0;k<BLOCK;k++){
                    for(int l=0;l<BLOCK;l++){
                        xi=(x+l)%N;
                        yi=(y+k)%M;
                        int pixel = srcImage.getRGB(xi, yi); // 下面三行代码将一个数字转换为RGB数字
                        rgb[0] = (pixel & 0xff0000) >> 16;
                        rgb[1] = (pixel & 0xff00) >> 8;
                        rgb[2] = (pixel & 0xff);
                        //  RGB->Lab
                        temp.set(0,rgb[0]);
                        temp.set(1,rgb[1]);
                        temp.set(2,rgb[2]);
                        rgbToLab(temp);
                        // L->T1,T2,T3
                        harrs[k][l]=temp.get(0);
                        // sum Xi Yi
                        Xi=Xi+1.0*xi/N;
                        Yi=Yi+1.0*yi/M;
                        // SUM Lab
                        for(int z=0;z<3;z++){
                            L[z]+=temp.get(z);
                            tempRGB[z]+=rgb[z];
                        }
                    }
                }
                //块位置求解
                Xi/=BLOCK_N;
                Yi/=BLOCK_N;
                //块Lab求解
                for(int z=0;z<3;z++){
                    L[z]/=BLOCK_N;
                    Lab.set(z,L[z]);
                    tempRGB[z]/=BLOCK_N;
                }
                //保存RGB值
                rgbs.add(Arrays.asList(tempRGB[0],tempRGB[1],tempRGB[2]));
                //纹理求解
                DWTTest t=new DWTTest();
                double[] lpd={0.5,0.5};
                double[] hpd={-0.5,0.5};
                List<Double[][]> harrsres=t.mydwt2(harrs,lpd,hpd);
                int len=harrsres.size();
                for(int z=0;z<len-1;z++){
                    Double[][] tempHarrs=harrsres.get(z);
                    double tempT=0;
                    for(int f=0;f<tempHarrs.length;f++){
                        for(int g=0;g<tempHarrs[0].length;g++){
                            tempT+=(tempHarrs[f][g]*tempHarrs[f][g]);
                        }
                    }
                    T[z]=0.5*Math.sqrt(tempT);
                }
                //构造特征向量
                List<Double> sample=new ArrayList<>();
                sample.add(L[0]);
                sample.add(L[1]);
                sample.add(L[2]);
                sample.add(T[0]);
                sample.add(T[1]);
                sample.add(T[2]);
                sample.add(Xi);
                sample.add(Yi);
                this.samples.add(sample);
            }
        }
        return this.samples;
    }

    public static void rgbToLab(List<Double> rgb){
        double R=rgb.get(0);
        double G=rgb.get(1);
        double B=rgb.get(2);
        double _R =  R / 255.0 ;        //R from 0 to 255
        double _G= G / 255.0 ;       //G from 0 to 255
        double _B= B / 255.0 ;     //B from 0 to 255

        if ( _R > 0.04045 ) _R = Math.pow(( _R + 0.055 ) / 1.055 , 2.4);
        else  _R = _R / 12.92;
        if ( _G > 0.04045 ) _G = Math.pow(( _G + 0.055 ) / 1.055 , 2.4);
        else _G = _G / 12.92;
        if ( _B > 0.04045 ) _B = Math.pow (( _B + 0.055 )/ 1.055 , 2.4);
        else _B = _B / 12.92;

        _R = _R * 100;
        _G = _G * 100;
        _B = _B * 100;

        double X = _R * 0.4124 + _G * 0.3576 + _B * 0.1805;
        double Y = _R * 0.2126 + _G * 0.7152 + _B * 0.0722;
        double Z = _R * 0.0193 + _G * 0.1192 + _B * 0.9505;

        X = X / 95.047  ;        //ref_X =  95.047   Observer= 2°, Illuminant= D65
        Y = Y / 100.000  ;        //ref_Y = 100.000
        Z = Z / 108.883   ;       //ref_Z = 108.883

        if ( X > 0.008856 ) X = Math.pow(X ,  1.0/3 );
        else X = ( 7.787 * X ) + ( 16.0 / 116 );
        if ( Y > 0.008856 ) Y = Math.pow(Y , 1.0/3 );
        else Y = ( 7.787 * Y ) + ( 16.0/ 116 );
        if ( Z > 0.008856 ) Z = Math.pow(Z ,  1.0/3 );
        else Z = ( 7.787 * Z ) + ( 16.0 / 116 );

        double L= ( 116 * Y ) - 16+0.5;
        double a = 500 * ( X - Y )+0.5;
        double b = 200 * ( Y - Z )+0.5;
        rgb.set(0,L);
        rgb.set(1,a);
        rgb.set(2,b);
    }
    public List<List<Double>> getRGBs(){
        return this.rgbs;
    }


    private static int D=8;
    private static int BLACK=20;
    private static int YELLOW=70;
    public final static int BLOCK=4;
    public final static int BLOCK_N=16;

}
