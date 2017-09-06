package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class OpenFilter extends ErosinFilter {
    public BufferedImage filter(BufferedImage sourceImage, BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        // 开操作：A-B+B
        // 进行腐蚀操作
        dest=super.filter(sourceImage,null);
        // 进行膨胀操作
        DilationFilter dilationFilter=new DilationFilter();
        dilationFilter.setStructureElements(this.getStructureElements());
        dest=dilationFilter.filter(dest,null);
        return dest;
    }
}
