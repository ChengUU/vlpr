package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

/**
 * Created by ChengXX on 2017/3/18.
 */
public class PixleStaticFilter extends AbstractBufferedImageOp {
    private double threshold;
    private boolean blankImage;

    public PixleStaticFilter(){
        threshold=1.0;
        blankImage=false;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isBlanlImage() {
        return blankImage;
    }

    public void setBlanlImage(boolean blanlImage) {
        this.blankImage = blanlImage;
    }

    public BufferedImage  filter(BufferedImage src, BufferedImage dest){
        int width=src.getWidth();
        int height=src.getHeight();
        blankImage=false;
        if(null==dest) dest=createCompatibleDestImage(src,null);
        int[] inPixles=new int[width*height];
        int[] outPixles=new int[width*height];
        getRGB(src,0,0,width,height,inPixles);
        int index=0;

        //calculate mean,min,max
        int max=0;
        int min=255;
        double sum=0.0;
        for(int row=0;row<height;row++){
            int tr=0;
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixles[index]>>16)&0xff;
                min=Math.min(min,tr);
                max=Math.max(max,tr);
                sum+=tr;
            }
        }
        double mean=sum/(width*height);
        //calculate standard deviation
        double stdev=0.0;
        double total=width*height;
        sum=0;
        for(int row=0;row<height;row++){
            int tr=0;
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixles[index]>>16)&0xff;
                sum+=tr*tr;
                outPixles[index]=(255<<24)|(tr<<16)|(tr<<8)|tr;
            }
        }
        stdev=(sum/total)-Math.pow(mean,2);
        if(stdev<=threshold) blankImage=true;
        setRGB(dest,0,0,width,height,outPixles);
        return dest;

    }
}
