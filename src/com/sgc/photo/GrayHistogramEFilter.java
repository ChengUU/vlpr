package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/24.
 */
public class GrayHistogramEFilter extends HistogramEFilter {
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        int totalPixelNumber=width*height;
        int L=256;
        int[] inPixels=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,0);
        int[] iDataBins=new int[L];
        int[] newBins=new int[L];
        Arrays.fill(iDataBins,0);
        Arrays.fill(newBins,0);
        int index=0;
        int tr=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixels[index]>>16)&0xff;
                iDataBins[tr]++;
            }
        }
        generateHEData(newBins,iDataBins,totalPixelNumber,L);
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=(inPixels[index]>>16)&0xff;
                output[index]=(255<<24)|(newBins[tr]<<16)|(newBins[tr]<<8)|newBins[tr];
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
}
