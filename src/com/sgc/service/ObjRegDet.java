package com.sgc.service;

import com.sgc.geometric.ConnRegion;
import com.sgc.photo.AbstractBufferedImageOp;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/25.
 */
public class ObjRegDet extends AbstractBufferedImageOp {
    public BufferedImage getObjReg(BufferedImage sourceImage, ConnRegion connRegion){
        int cx=connRegion.getX();
        int cy=connRegion.getY();
        int cw=connRegion.getWidth();
        int ch=connRegion.getHeight();
        int[] inPixels=new int[cw*ch];
        getRGB(sourceImage,cx,cy,cw,ch,inPixels);
        BufferedImage dest=new BufferedImage(cw,ch,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,cw,ch,inPixels);
        return dest;
    }
}
