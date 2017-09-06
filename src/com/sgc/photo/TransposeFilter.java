package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/29.
 */
public class TransposeFilter extends AbstractBufferedImageOp {
    /**
     *  this function apply to transposition image
     * @param sourceImage
     * @param dest
     * @return
     */
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        if(null==dest) dest=new BufferedImage(height,width,sourceImage.TYPE_INT_RGB);
        int[] inPixels=new int[width*height];
        int[] output=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        Arrays.fill(output,-16777216);
        int in_index=0;
        int out_index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                in_index=row*width+col;
                out_index=col*height+row;
                output[out_index]=inPixels[in_index];
            }
        }
        setRGB(dest,0,0,height,width,output);
        return dest;
    }
}
