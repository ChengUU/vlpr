package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/27.
 */
public class HatFilter {
    public BufferedImage onTopHat(BufferedImage image,int[][] b){
        GrayOpenFilter grayOpenFilter=new GrayOpenFilter();
        grayOpenFilter.setStructureElements(b);
        BasicOperatorFilter basicOperatorFilter=new BasicOperatorFilter();
        return basicOperatorFilter.minus(image,grayOpenFilter.filter(image,null));
    }
    public BufferedImage onBotHat(BufferedImage image,int[][] b){
        GrayOpenFilter grayOpenFilter=new GrayOpenFilter();
        grayOpenFilter.setStructureElements(b);
        BasicOperatorFilter basicOperatorFilter=new BasicOperatorFilter();
        return basicOperatorFilter.minus(grayOpenFilter.filter(image,null),image);
    }
}
