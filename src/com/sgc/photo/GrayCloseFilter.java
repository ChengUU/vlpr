package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/27.
 */
public class GrayCloseFilter extends GrayDilationFilter {
    @Override
    public BufferedImage filter(BufferedImage sourceImage, BufferedImage dest) {
        // 进行膨胀运算
        dest= super.filter(sourceImage, dest);
        // 进行腐蚀运算
        GrayErosinFilter grayErosinFilter=new GrayErosinFilter();
        grayErosinFilter.setStructureElements(this.getStructureElements());
        dest=grayErosinFilter.filter(dest,null);
        return dest;
    }
}
