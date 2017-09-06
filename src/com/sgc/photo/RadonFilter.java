package com.sgc.photo;

import com.sgc.res.RadonRes;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/28.
 */
public class RadonFilter extends AbstractBufferedImageOp{
    public static final double deg2rad=3.14159265358979/180;

    /**
     *
     * @param image it must be a binary image
     * @param minAngle the min angle
     * @param maxAngle the max angle
     * @param step the length of count
     * @return it's an object of the class RadonRes
     */
    public RadonRes radon(BufferedImage image, int minAngle, int maxAngle, int step){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        /*获取图像数据*/
        getRGB(image,0,0,width,height,inPixels);
        /*变换角度数*/
        int numAngles=(maxAngle-minAngle)/step;
        /*初始化扫描角度数组*/
        double[] theta=new double[numAngles];
        double[] degree=new double[numAngles];
        degree[0]=minAngle;
        theta[0]=degree[0]*deg2rad;
        // convert the degree to radian
        for(int i=1;i<numAngles;i++){
            degree[i]=degree[i-1]+step;
            theta[i]=degree[i]*deg2rad;
        }

        //求取数据矩阵的中心
        int xOrign=Math.max(0,(width-1)/2);
        int yOrign=Math.max(0,(height-1)/2);
        //获取距离长度
        int temp1=width-1-xOrign;
        int temp2=height-1-yOrign;
        int rLast=(int)Math.ceil(Math.sqrt((double)(temp1*temp1+temp2*temp2)))+1;
        int rFirst=-rLast;
        // 距离图像中心最远的距离,即极坐标系中的ro
        int rSize=rLast-rFirst+1;
        // 计算所有ro
        int[] xp=new int[rSize];
        for(int i=rFirst;i<=rLast;i++) xp[i+rLast]=i;
        // radon变换结果
        double[] pPtr=new double[numAngles*rSize];
        Arrays.fill(pPtr,0.0);
        radon(pPtr,inPixels,theta,height,width,xOrign,yOrign,numAngles,rFirst,rSize);
        return new RadonRes(pPtr,xp,degree);
    }

    /**
     *
     * @param pr 进行radon变换后输出矩阵对每一theta角均对应一列数据
     * @param pixel 当前进行radon变换点的像素值(由于每一个像素点分解为相邻的四个点进行运算,因此在计算pixel时候也要乘以0.25)
     * @param r 距离坐标原点之间的距离
     */
    private static void incrementRadon(double[] pr,double pixel,int radian,double r){
        int r1=(int)r;
        double delta=r-r1;
        r1+=radian;

        //在此采用两个点进行运算以保证计算的精度
        pr[r1]+=(pixel*(1-delta));
        pr[r1+1]+=(pixel*delta);
    }

    /**
     *
     * @param pPtr radon变换的结果
     * @param iPtr 图像数据矩阵
     * @param thetaPtr 变换角度数据
     * @param M 图像数据矩阵的行数
     * @param N 图像数据矩阵的列数
     * @param xOrign 图像数据矩阵中心横坐标(列)
     * @param yOrign  图像数据矩阵中心纵坐标(行)
     * @param numAngles 进行变换的角度总数
     * @param rFirst 极坐标中初始点到变换原点的距离
     * @param rSize 整个radon变换中极坐标的点之间的最远距离(对角线)
     */
    public static void radon(double[] pPtr,int[] iPtr,double[] thetaPtr,int M,int N,int xOrign,int yOrign,int numAngles,int rFirst,int rSize){
        int k,m,n; /* loop counters */
        double angle; /*radian angle value*/
        double cosine,sine; /* cosine and sine of current angle */
        double pixel; /* current pixel value */
        int pixelPtr;
        int pr;
        double[] xCosTable,ySinTable; /* 极坐标转换快速查找表 */
        /* tables for x*cos(angle) and y*sin(angle) */
        double x,y;
        double r;
        xCosTable=new double[2*N];
        ySinTable=new double[2*M];
        // 每一个theta角经过radon变换后就会产生一列数据,这一列数据中共有rSize个数据
        for(k=0;k<numAngles;k++){
            pr=k*rSize;
            angle=thetaPtr[k];
            cosine=Math.cos(angle);
            sine=Math.sin(angle);
            /**
             * radon变换：极坐标中，沿r轴的theta角和每一个像素点的分布都是非线性的，而此处采用的是线性变换，
             * 为了提高精度，把每一个像素分解成相邻的四个像素点进行计算，X,Y坐标的误差为0.25
             */
            for(n=0;n<N;n++){
                x=n-xOrign;
                xCosTable[2*n]=(x-0.25)*cosine;
                xCosTable[2*n+1]=(x+0.25)*cosine;
            }

            for(m=0;m<M;m++){
                y=m-yOrign;
                ySinTable[2*m]=(y-0.25)*sine;
                ySinTable[2*m+1]=(y+0.25)*sine;
            }
            pixelPtr=0;

            for(m=0;m<M;m++){// 进行行遍历
             for(n=0;n<N;n++){// 进行列遍历
                    pixel=(-1==iPtr[pixelPtr++])?1:0.0;
                    if(pixel!=0.0){
                        pixel*=0.25;
                        // 将一个像素点分解成相邻的四个点进行计算
                        r=xCosTable[2*n]+ySinTable[2*m]-rFirst;
                        incrementRadon(pPtr,pixel,pr,r);

                        r=xCosTable[2*n]+ySinTable[2*m+1]-rFirst;
                        incrementRadon(pPtr,pixel,pr,r);

                        r=xCosTable[2*n+1]+ySinTable[2*m]-rFirst;
                        incrementRadon(pPtr,pixel,pr,r);

                        r=xCosTable[2*n+1]+ySinTable[2*m+1]-rFirst;
                        incrementRadon(pPtr,pixel,pr,r);
                    }
                }
            }
        }
    }
}
