package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/4/9.
 */
public class GaussianBlurFilter extends AbstractBufferedImageOp {


    public double[][] get2DKernelData(int n, double sigma) {
        int size = 2 * n + 1;
        double sigma2 = 2 * sigma * sigma;
        double sigma2PI = sigma2 * Math.PI;
        double[][] kernelData = new double[size][size];
        int row = 0;
        for (int i = -n; i <= n; i++) {
            int column = 0;
            for (int j = -n; j <= n; j++) {
                double xDistance = i * i;
                double yDistance = j * j;
                kernelData[row][column] = Math.exp(-(xDistance + yDistance) / sigma2) / sigma2PI;
                column++;
            }
            row++;
        }
        return kernelData;
    }

    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        int[] inPixels=new int[width*height];
        int[] outPixels=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        // 获取高斯参数
        double[][] kernelData=get2DKernelData(n,sigma);
        int kwRadius=n;
        int khRadius=n;
        int index=0;
        double weight=0;
        for(int row=0;row<height;row++){
            int ta=0,tr=0,tg=0,tb=0;

            for(int col=0;col<width;col++){
                index=row*width+col;
                double weightSum=0D;
                double redSum=0D,greenSum=0D,blueSum=0D;
                ta=255;
                for(int subRow=-khRadius;subRow<=khRadius;subRow++){
                    int nrow=row+subRow;
                    if(nrow<0||nrow>=height) nrow=row-subRow;
                    for(int subCol=-kwRadius;subCol<=kwRadius;subCol++){
                        int ncol=col+subCol;
                        if(ncol<0||ncol>=width) ncol=col-subCol;
                        int index2=nrow*width+ncol;
                        int ta1=(inPixels[index2]>>24)&0xff;
                        int tr1=(inPixels[index2]>>16)&0xff;
                        int tg1=(inPixels[index2]>>8)&0xff;
                        int tb1=inPixels[index2]&0xff;
                        weight=kernelData[subRow+khRadius][subCol+kwRadius];
                        redSum+=tr1*weight;
                        greenSum+=tg1*weight;
                        blueSum+=tb1*weight;
                        weightSum+=weight;
                    }
                }
                tr=(int)(redSum/weightSum);
                tg=(int)(greenSum/weightSum);
                tb=(int)(blueSum/weightSum);
                outPixels[index]=(ta<<24)|(tr<<16)|(tg<<8)|tb;
                ta=tr=tg=tb=0;
            }
        }
        setRGB(dest,0,0,width,height,outPixels);
        return dest;
    }

    private int n;
    private double sigma;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public GaussianBlurFilter(){
        this(DEFAULT_GAUSSIAN_SIZE);
    }
    public GaussianBlurFilter(int n){
        this.n=n;
        this.sigma=1;
    }

    public final static int DEFAULT_GAUSSIAN_SIZE=5;
}
