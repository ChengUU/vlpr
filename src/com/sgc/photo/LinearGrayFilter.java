package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/21.
 */
public class LinearGrayFilter extends AbstractBufferedImageOp{
    private double[] parameters=null;
    public LinearGrayFilter(){}
    public void setParameters(double[] parameters){this.parameters=parameters;}
    public double[] getParameters(){return this.parameters;}
    public BufferedImage filter(BufferedImage sourceimage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceimage,null);
        int width=sourceimage.getWidth();
        int height=sourceimage.getHeight();
        int[] inPixels=new int[width*height];
        int[] outPixels=new int[width*height];
        double A=parameters[2]/parameters[0];
        double B=(parameters[3]-parameters[2])/(parameters[1]-parameters[0]);
        double C=(255-parameters[3])/(255-parameters[1]);
        getRGB(sourceimage,0,0,width,height,inPixels);
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                int index=row*width+col;
                int ta=(inPixels[index]>>24)&0xff;
                int pixel=(inPixels[index]>>16)&0xff;
                if(pixel<parameters[0]){
                    pixel=(int)(A*pixel);
                }else if(pixel>parameters[1]){
                    pixel=(int)(C*(pixel-parameters[1])+parameters[3]);
                }else{
                    pixel=(int)(B*(pixel-parameters[0]));
                }
                outPixels[index]=(ta<<24)|(pixel<<16)|(pixel<<8)|pixel;
            }
        }
        setRGB(dest,0,0,width,height,outPixels);
        return dest;
    }
}
