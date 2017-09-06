package com.sgc.photo;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class ConnCompFilter extends AbstractBufferedImageOp{
    private int[][] se=new int[][]{{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};

    public int[][] getSe() {
        return se;
    }

    public void setSe(int[][] se) {
        this.se = se;
    }

    public BufferedImage filter (BufferedImage srcimg, BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(srcimg,null);
        CloseFilter closeFilter=new CloseFilter();
        closeFilter.setStructureElements(se);
        srcimg=closeFilter.filter(srcimg,null);
        OpenFilter openFilter=new OpenFilter();
        openFilter.setStructureElements(se);
        srcimg=openFilter.filter(srcimg,null);
        dest=openFilter.filter(srcimg,null);
        return dest;

    }
}
