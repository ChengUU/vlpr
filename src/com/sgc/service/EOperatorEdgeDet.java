package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/4/12.
 */
public class EOperatorEdgeDet extends AbstractBufferedImageOp {

    public final static int[][] se={{-1,-1,0,1,1}};

    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        int index=0,index2=0;
        int hRadius=se.length/2;
        int wRadius=se[0].length/2;
        System.out.println("hRadius:"+hRadius+"wRadius:"+wRadius);
        int tr=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int graySum=0;
                for(int subRow=-hRadius;subRow<=hRadius;subRow++){
                    int nrow=row+subRow;
                    if(nrow<0||nrow>=height) nrow=row-subRow;
                    for(int subCol=-wRadius;subCol<=wRadius;subCol++){
                        int ncol=col+subCol;
                        if(ncol<0||ncol>=width) ncol=col-subCol;
                        index2=nrow*width+ncol;
                        tr=(inPixels[index2]>>16)&0xff;
                        graySum+=Math.abs(tr*se[hRadius+subRow][wRadius+subCol]);
                    }
                }
                graySum=clamp(graySum);
                output[index]=(255<<24)|(graySum<<16)|(graySum<<8)|graySum;
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
}
