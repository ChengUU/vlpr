package com.sgc.photo;

import com.sgc.service.ColorPicker;
import com.sgc.util.Util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/4/12.
 */
public class LayerGrayFilter extends AbstractBufferedImageOp {
    private ColorPicker[] colorPickers;
    private int layerNum;
    private int[] grayValue;
    private BufferedImage destGrayImage;
    private int step;

    public LayerGrayFilter(ColorPicker[] colorPickers) {
        this.colorPickers = colorPickers;
        layerNum=this.colorPickers.length;
        grayValue=new int[layerNum+1];
        step=(int)(255.0/(layerNum+1));
        for(int i=layerNum;i>=0;i--){
            grayValue[layerNum-i]=i*step;
        }
    }
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if (null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(sourceImage,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,0);
        int index=0;
        int layer=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int r=(inPixels[index]>>16)&0xff;
                int g=(inPixels[index]>>8)&0xff;
                int b=inPixels[index]&0xff;
                float[] hsv= Util.rgbToHSV(r,g,b);
                layer=layerNum;
                for(int i=0;i<layerNum;i++){
                    if(colorPickers[i].check(hsv)) layer=i;
                }
                output[index]=(255<<24)|(grayValue[layer]<<16)|(grayValue[layer]<<8)|grayValue[layer];
            }
        }
        destGrayImage=createCompatibleDestImage(sourceImage,null);
        setRGB(dest,0,0,width,height,output);
        setRGB(destGrayImage,0,0,width,height,output);
        return dest;
    }
    public BufferedImage[] binaryImage(){
        int width=destGrayImage.getWidth();
        int height=destGrayImage.getHeight();
        BufferedImage[] binaryImage=new BufferedImage[2];
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        int[] inPixels=new int[width*height];
        getRGB(destGrayImage,0,0,width,height,inPixels);
        int index=0;
        for(int i=0;i<2;i++){
            // 输出矩阵初始化
            Arrays.fill(output,-16777216);
            for(int row=0;row<height;row++){
                for(int col=0;col<width;col++){
                    index=row*width+col;
                    int tr=(inPixels[index]>>16)&0xff;
                    if(tr==(layerNum-i)*step){
                        output[index]=-1;
                    }
                }
            }
            // 保存图像数据
            binaryImage[i]=createCompatibleDestImage(destGrayImage,null);
            setRGB(binaryImage[i],0,0,width,height,output);
        }
        return binaryImage;
    }
}
