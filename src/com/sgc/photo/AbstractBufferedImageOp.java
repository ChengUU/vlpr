package com.sgc.photo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ChengXX on 2017/3/11.
 */
public class AbstractBufferedImageOp implements BufferedImageOp {
    public int clamp(int value){
        return value>255?255:(value<0?0:value);
    }
    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    public static int[] getRGB(BufferedImage image,int x,int y,int width,int height,int[] pixles){
        int type=image.getType();
        if(type==BufferedImage.TYPE_INT_ARGB||type==BufferedImage.TYPE_INT_RGB)
            return (int[])image.getRaster().getDataElements(x,y,width,height,pixles);
        return image.getRGB(x,y,width,height,pixles,0,width);
    }
    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public static void setRGB(BufferedImage image,int x,int y,int width,int height,int[] pixles){
        int type=image.getType();
        if(type==BufferedImage.TYPE_INT_ARGB||type==BufferedImage.TYPE_INT_RGB)
            image.getRaster().setDataElements(x,y,width,height,pixles);
        else image.setRGB(x,y,width,height,pixles,0,width);
    }

    /**
     * 使用双线性插值法进行像素值提取
     * @param input
     * @param width
     * @param height
     * @param prow
     * @param pcol
     * @return
     */
    public int[] getPixel(int[] input,int width,int height,double prow,double pcol){
        double row=Math.floor(prow);
        double col=Math.floor(pcol);
        // 如果当前点已经在原图像之外
        if(row<0||row>=height||col<0||col>=width){
            return new int[]{BACKGROUND.getRed(),BACKGROUND.getGreen(),BACKGROUND.getBlue()};
        }
        double u=pcol-col;
        double v=prow-row;
        // 计算下一个点的横纵坐标
        int nextCol=(int)(col+1);
        if(nextCol>=width) nextCol=(int)col;
        int nextRow=(int)(row+1);
        if(nextRow>=height) nextRow=(int)row;
        // 双线性插值四个点的坐标
        int index1=(int)(row*width+col);
        int index2=(int)(row*width+nextCol);
        int index3=(int)(nextRow*width+col);
        int index4=(int)(nextRow*width+nextCol);

        int tr1,tr2,tr3,tr4;
        int tg1,tg2,tg3,tg4;
        int tb1,tb2,tb3,tb4;

        tr1=(input[index1]>>16)&0xff;
        tg1=(input[index1]>>8)&0xff;
        tb1=input[index1]&0xff;

        tr2=(input[index2]>>16)&0xff;
        tg2=(input[index2]>>8)&0xff;
        tb2=input[index2]&0xff;

        tr3=(input[index3]>>16)&0xff;
        tg3=(input[index3]>>8)&0xff;
        tb3=input[index3]&0xff;

        tr4=(input[index4]>>16)&0xff;
        tg4=(input[index4]>>8)&0xff;
        tb4=input[index4]&0xff;

        int tr=(int)(v*u*tr4+v*(1-u)*tr3+(1-v)*u*tr2+(1-u)*(1-v)*tr1);
        int tg=(int)(v*u*tg4+v*(1-u)*tg3+(1-v)*u*tg2+(1-u)*(1-v)*tg1);
        int tb=(int)(v*u*tb4+v*(1-u)*tb3+(1-v)*u*tb2+(1-u)*(1-v)*tb1);

        return new int[]{tr,tg,tb};
    }

    public BufferedImage createBufferedImage(BufferedImage src){
        ColorModel cm=src.getColorModel();
        int width=src.getWidth();
        int height=src.getHeight();
        int[] pixles=new int[width*height];
        getRGB(src,0,0,width,height,pixles);
        BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster(width,height),cm.isAlphaPremultiplied(),null);
        setRGB(image,0,0,width,height,pixles);
        return image;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        return null;
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0,0,src.getWidth(),src.getHeight());
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        if(null==destCM){
            destCM=src.getColorModel();
        }
        return new BufferedImage(destCM,destCM.createCompatibleWritableRaster(src.getWidth(),src.getHeight()),
                destCM.isAlphaPremultiplied(),null);
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if(null==dstPt) dstPt=new Point2D.Double();
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }

    public static void writeImageFile(BufferedImage bi)throws IOException{
       int width= bi.getWidth();
       int height=bi.getHeight();
       SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        GregorianCalendar now=new GregorianCalendar();
        long time=now.getTimeInMillis();
       String fname=String.valueOf(time);
       StringBuffer fnameb=new StringBuffer(OBJ_PATH);
       fnameb.append(fname);
       fnameb.append(width);
       fnameb.append(height);
       fnameb.append(".png");
       File f=new File(fnameb.toString());
        ImageIO.write(bi,IMG_PNG,f);
    }
    public static void writeImageFile(BufferedImage bi,String fpath)throws IOException{
        StringBuffer fnameb=new StringBuffer(fpath);
        fnameb.append(".png");
        File f=new File(fnameb.toString());
        ImageIO.write(bi,IMG_PNG,f);
    }


    public static final double clo60=1.0/60.0;
    public static final double clo255=1.0/255.0;

    public static final String OBJ_PATH="D:\\ChengXX\\LPR\\Templat\\";
    public static final String IMG_JPG="jpg";
    public static final String IMG_PNG="png";

    public static final Color BACKGROUND=Color.BLACK;
}
