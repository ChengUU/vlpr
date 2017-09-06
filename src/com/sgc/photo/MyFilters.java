package com.sgc.photo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by ChengXX on 2017/3/11.
 */
class MyFilters extends JPanel {
    private BufferedImage image=null;
    private BufferedImage dest=null;

    public MyFilters(){
        try{
            File f=new File("D:\\ChengXX\\Cluster\\Lena256256.jpg");
            image= ImageIO.read(f);
        }catch(IOException e){e.printStackTrace();}
    }

    public void setImage(BufferedImage image){
        this.image=image;
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        g2d.clearRect(0,0,this.getWidth(),this.getHeight());
        if(null!=this.image){
            g2d.drawImage(this.image,0,0,image.getWidth(),image.getHeight(),null);
            if(null!=dest){
                g2d.drawImage(dest,image.getWidth()+10,0,dest.getWidth(),dest.getHeight(),null);
            }
        }
    }

    public BufferedImage doColorGray(BufferedImage image){
        ColorConvertOp filterObj=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
        return filterObj.filter(image,dest);
    }

    public void doBinaryImage(BufferedImage image){
        doColorGray(image);
        byte[] threshold=new byte[256];
        for(int i=0;i<threshold.length;i++){
            threshold[i]=(i<128)?(byte)0:(byte)255;
        }
        BufferedImageOp thresholdOp=new LookupOp(new ByteLookupTable(0,threshold),null);
        dest=thresholdOp.filter(image,null);
    }
    public void doBlur(BufferedImage image){
        //fix issue-unable to convolve src image
        if(image.getType()==BufferedImage.TYPE_3BYTE_BGR){
            image=convertType(image,BufferedImage.TYPE_INT_RGB);
        }
        float ninth=1.0f/9.0f;
        float[] blurKernel={ninth,ninth,ninth,
                            ninth,ninth,ninth,
                            ninth,ninth,ninth};
        BufferedImageOp blurFilter=new ConvolveOp(new Kernel(3,3,blurKernel));
        dest=blurFilter.filter(image,null);
    }
    public BufferedImage doLookUp(BufferedImage image){
        byte[][] lookupData=new byte[3][256];
        for(int cnt=0;cnt<256;cnt++){
            lookupData[0][cnt]=(byte)(255-cnt);
            lookupData[1][cnt]=(byte)(255-cnt);
            lookupData[2][cnt]=(byte)(255-cnt);
        }
        ByteLookupTable lookupTable=new ByteLookupTable(0,lookupData);
        BufferedImageOp filterObj=new LookupOp(lookupTable,null);
        return filterObj.filter(image,null);
    }

    public void doScale(BufferedImage image,double sx,double sy){
        AffineTransformOp atfFilter=new AffineTransformOp(AffineTransform.getScaleInstance(sx,sy),AffineTransformOp.TYPE_BILINEAR);
        int nw=(int)(image.getWidth()*sx);
        int nh=(int)(image.getHeight()*sy);
        BufferedImage result=new BufferedImage(nw,nh,BufferedImage.TYPE_3BYTE_BGR);
        dest=atfFilter.filter(image,result);
    }
    public static BufferedImage convertType(BufferedImage image,int type){
        ColorConvertOp cco=new ColorConvertOp(null);
        BufferedImage dest=new BufferedImage(image.getWidth(),image.getHeight(),type);
        cco.filter(image,dest);
        return dest;
    }
}
