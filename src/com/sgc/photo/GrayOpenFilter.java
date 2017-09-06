package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/27.
 */
public class GrayOpenFilter extends GrayErosinFilter {
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        // 获取腐蚀图像数据
        dest=super.filter(sourceImage,null);
        // 膨胀运算
        GrayDilationFilter grayDilationFilter=new GrayDilationFilter();
        grayDilationFilter.setStructureElements(this.getStructureElements());
        dest=grayDilationFilter.filter(dest,null);
        return dest;
    }
}
