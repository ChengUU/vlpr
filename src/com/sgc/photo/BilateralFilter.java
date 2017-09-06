package com.sgc.photo;


import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/4/27.
 */
public class BilateralFilter extends AbstractBufferedImageOp {
    private final static double factor=-0.5d;
    private double sigmas;// space
    private double sigmar; // range
    private int radius;
    private double[][] sWeighttable;
    private double[] rWeightTable;

    public BilateralFilter(){
        this.sigmar=30;
        this.sigmas=3;
        this.radius=3;
    }

    public static double getFactor() {
        return factor;
    }

    public double getSigmas() {
        return sigmas;
    }

    public void setSigmas(double sigmas) {
        this.sigmas = sigmas;
    }

    public double getSigmar() {
        return sigmar;
    }

    public void setSigmar(double sigmar) {
        this.sigmar = sigmar;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    private void buildSpaceWeighttable(){
        int size=2*radius+1;
        sWeighttable=new double[size][size];
        for(int sr=-radius;sr<=radius;sr++){
            for(int sc=-radius;sc<=radius;sc++){
                // 计算欧几里德距离
                double delta=Math.sqrt(sr*sr+sc*sc)/sigmas;
                // 根据一维高斯公式计算高斯权重系数
                double deltaDelta=delta*delta;
                int row=sr+radius;
                int col=sc+radius;
                sWeighttable[row][col]=Math.exp(deltaDelta*factor);
            }
        }
    }

    private void buildRangeWeightTable(){
        // 像素值范围是[0,255]
        rWeightTable=new double[256];
        // 计算像素值得高斯权重
        for(int i=0;i<256;i++){
            double delta=Math.sqrt(i*i)/sigmar;
            double deltaDelta=delta*delta;
            rWeightTable[i]=Math.exp(deltaDelta*factor);
        }
    }
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (null == dest) dest = createCompatibleDestImage(src, null);
        int width = src.getWidth();
        int height = src.getHeight();
//        radius = (int) Math.max(sigmas, sigmar);
        buildRangeWeightTable();
        buildSpaceWeighttable();
        int index = 0;
        int[] inPixels = new int[width * height];
        int[] output = new int[width * height];
        getRGB(src, 0, 0, width, height, inPixels);
        double redSum = 0, greenSum = 0, blueSum = 0;
        double csRedWeight = 0, csGreenWeight = 0, csBlueWeight = 0;
        double csSumRedWeight = 0, csSumGreenWeight = 0, csSumBlueWeight = 0;
        int ta = 0, tr = 0, tg = 0, tb = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                int rowoffset = 0, coloffset = 0;
                int index2 = 0;
                int ta2, tr2 = 0, tg2 = 0, tb2 = 0;
                for (int rows = -radius; rows <= radius; rows++) {
                    rowoffset = row + rows;
                    if (rowoffset < 0 || rowoffset >= height) rowoffset = row - rows;
                    for (int cols = -radius; cols <= radius; cols++) {
                        coloffset = col + cols;
                        if (coloffset < 0 || coloffset >= width) coloffset = col - cols;
                        index2 = rowoffset * width + coloffset;
                        ta2 = (inPixels[index2] >> 24) & 0xff;
                        tr2 = (inPixels[index2] >> 16) & 0xff;
                        tg2 = (inPixels[index2] >> 8) & 0xff;
                        tb2 = inPixels[index2] & 0xff;
                        // 在查找表中获取对应权重
                        csRedWeight = sWeighttable[radius + rows][radius + cols] * rWeightTable[Math.abs(tr2 - tr)];
                        csGreenWeight = sWeighttable[radius + rows][radius + cols] * rWeightTable[Math.abs(tg2 - tg)];
                        csBlueWeight = sWeighttable[radius + rows][radius + cols] * rWeightTable[Math.abs(tb2 - tb)];
                        //累加权重和
                        csSumRedWeight += csRedWeight;
                        csSumGreenWeight += csGreenWeight;
                        csSumBlueWeight += csBlueWeight;
                        // 累加权重像素和
                        redSum += (csRedWeight * (double) tr2);
                        greenSum += (csGreenWeight * (double) tg2);
                        blueSum += (csBlueWeight * (double) tb2);
                    }
                }
                tr = (int) (Math.floor(redSum / csSumRedWeight));
                tg = (int) (Math.floor(greenSum / csSumGreenWeight));
                tb = (int) (Math.floor(blueSum / csSumBlueWeight));
                output[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);
                // 清空变量
                redSum = greenSum = blueSum = 0;
                csRedWeight = csGreenWeight = csBlueWeight = 0;
                csSumRedWeight = csSumGreenWeight = csSumBlueWeight = 0;
            }
        }
        setRGB(dest, 0, 0, width, height, output);
        return dest;
    }
}
