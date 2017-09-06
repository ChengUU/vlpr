package com.sgc.service;

import java.awt.image.BufferedImage;

import com.sgc.photo.*;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class EdgeDet extends AbstractBufferedImageOp{
    public static final int[][] DEFAULT_SE_A=new int[][]{{0,1,0},
                                                        {1,1,1},
                                                        {0,1,0}};
    public static final int[][] DEFAULT_SE_B=new int[][]{{1,1,1,1,1},
                                                        {1,1,1,1,1},
                                                        {1,1,1,1,1},
                                                        {1,1,1,1,1},
                                                        {1,1,1,1,1}};
    public static final float SIGMA=0.8f;
    private int[][] A=DEFAULT_SE_A;
    private int[][] B=DEFAULT_SE_B;
    private double sigma=SIGMA;

    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        OpenFilter openFilter=new OpenFilter();
        // 第一个括号
        openFilter.setStructureElements(A);
        BufferedImage a1=openFilter.filter(sourceImage,null);

        CloseFilter closeFilter=new CloseFilter();
        // 第二个括号
        closeFilter.setStructureElements(A);
        BufferedImage a2=closeFilter.filter(a1,null);
        // 第三个括号
        DilationFilter dilationFilter=new DilationFilter();
        dilationFilter.setStructureElements(B);
        BufferedImage a3=dilationFilter.filter(a2,null);
        // 前半部分结束
        openFilter.setStructureElements(B);
        BufferedImage a4=openFilter.filter(a3,null);

        // 后半部分
        closeFilter.setStructureElements(B);
        BufferedImage a5=closeFilter.filter(a1,null);

        // 基本操作运算符
        BasicOperatorFilter basicOperatorFilter=new BasicOperatorFilter();
        // 获得第一个函数值

        BufferedImage yd=basicOperatorFilter.minus(a4,a5);

        ErosinFilter erosinFilter=new ErosinFilter();
        erosinFilter.setStructureElements(B);
        BufferedImage a6=erosinFilter.filter(a2,null);
        closeFilter.setStructureElements(B);
        BufferedImage a7=closeFilter.filter(a6,null);
        BufferedImage ye=basicOperatorFilter.minus(a5,a7);

       BufferedImage a8=basicOperatorFilter.add(yd,ye);
       BufferedImage max=basicOperatorFilter.max(yd,ye);
       BufferedImage min=basicOperatorFilter.min(yd,ye);
       BufferedImage a9=basicOperatorFilter.minus(max,min);
       a9=basicOperatorFilter.scale(a9,sigma);
       dest=basicOperatorFilter.add(a8,a9);
       return dest;
    }

    public int[][] getA() {
        return A;
    }

    public void setA(int[][] a) {
        A = a;
    }

    public int[][] getB() {
        return B;
    }

    public void setB(int[][] b) {
        B = b;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}
