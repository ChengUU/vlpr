package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class CloseFilter extends DilationFilter {
    public BufferedImage filter(BufferedImage sourceImage, BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        // A.B=A+B-B
        // 进行膨胀操作
        dest=super.filter(sourceImage,null);
        // 进行腐蚀操作
        ErosinFilter erosinFilter=new ErosinFilter();
        erosinFilter.setStructureElements(this.getStructureElements());
        dest=erosinFilter.filter(dest,null);
        return dest;
    }
}
