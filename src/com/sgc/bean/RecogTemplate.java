package com.sgc.bean;

import com.sgc.photo.AbstractBufferedImageOp;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/4/21.
 */
public class RecogTemplate extends AbstractBufferedImageOp{
    private final int width;
    private final int height;
    private final int[] matrix;
    private final String repChar;
    public RecogTemplate(BufferedImage image,String repChar){
        this.width=image.getWidth();
        this.height=image.getHeight();
        this.matrix=new int[width*height];
        getRGB(image,0,0,width,height,matrix);
        init(matrix);
        this.repChar=repChar;
    }
    private void init(int[] matrix){
        int len=matrix.length;
        int tr=0;
        for(int i=0;i<len;i++){
            tr=(matrix[i]>>16)&0xff;
            if(tr>127) matrix[i]=1;
            else  matrix[i]=0;
        }
    }

    public double recg(BufferedImage image)throws Exception{
        int nw=image.getWidth();
        int nh=image.getHeight();
        if(width!=nw||height!=nh) throw new IllegalArgumentException("Two pictures of different sizes......");
        int[] pixels=new int[width*height];
        getRGB(image,0,0,width,height,pixels);
        init(pixels);
        // 计算距离及相关系数
        int len=matrix.length;
        int a=0,b=0,c=0;
        for(int i=0;i<len;i++){
            if(0!=matrix[i]) a++;
            if(0!=pixels[i]) b++;
            c+=(matrix[i]*pixels[i]);
        }
        double rate=1.0*c/(Math.sqrt(a)*Math.sqrt(b));
        return rate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getMatrix() {
        return matrix;
    }

    public String getRepChar() {
        return repChar;
    }
}
