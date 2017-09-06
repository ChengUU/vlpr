package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/26.
 */
public class HoughFilter extends AbstractBufferedImageOp {
    private int[][] count;
    /**
     * 进行Hough变换
     * this image must be a binary image.
     * @param image
     */
    public void getCount(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] inPixels = new int[width * height];
        getRGB(image, 0, 0, width, height, inPixels);
        int cosV, sinV;
        //求取距离原点的最大距离
        int dismax = (int) Math.sqrt(width * width + height * height);
        count = new int[180][dismax];
        // 不同角度且不同距离所对应的点数初始值为0
        for(int i=0;i<180;i++){
            for(int j=0;j<dismax;j++){
                count[i][j]=0;
            }
        }
        // 统计对应角度和距离的点的个数
        int index = 0;
        // 距离原点的距离
        int ro;
        // 与水平方向的夹角
        int radian;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                if (-1 == inPixels[index]) {
                    for (radian = 0; radian < 180; radian++) {
                        cosV = (int) (Math.cos(Math.PI * radian / 180) * 2048);
                        sinV = (int) (Math.sin(Math.PI * radian / 180) * 2048);
                        ro = (col * cosV + row * sinV) >> 11;
                        if (ro < dismax && ro > 0){
                            count[radian][ro] += 1;
                        }
                    }
                }
            }
        }
    }

    public void getHorizonRadian(){
        int themax = 0;
        int maxtheta =0;
        int maxro=0;
        // 直线总数
        int k = 0;
        // 距离原点距离
        int dismax=count[0].length;
        int firstDisLimit=(int)(dismax*0.1);
        int secondDisLimit=(int)(dismax*0.9);
        do {
            themax = 0;
            for (int i = 45; i < 136; i++) {
                for (int j = 0; j <=firstDisLimit; j++) {
                    if (count[i][j] > 30 && count[i][j] > themax) {// 寻找当前最最长直线
                        themax = count[i][j];
                        maxtheta= i;
                        maxro = j;
                    }
                }
            }
            for (int i = 45; i < 136; i++) {
                for (int j = secondDisLimit; j <dismax; j++) {
                    if (count[i][j] > 30 && count[i][j] > themax) {// 寻找当前最最长直线
                        themax = count[i][j];
                        maxtheta= i;
                        maxro = j;
                    }
                }
            }
            if (themax > 0) {// 将已找到的最大的直线标记为已访问
                int t1 = maxtheta;
                int t2 = maxro;
                count[t1][t2] = 0;
                k++;
            } else //已没有符合条件的直线
                break;
        } while (k < 180 * dismax);
    }


    public final static int N=500000;
}
