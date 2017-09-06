package com.sgc.photo;

import com.sgc.geometric.Point2D;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class  ErosinFilter extends DilationFilter{
    private int variation;
    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();

        int[] setA=new int[width*height];
        int[] output=new int[width*height];
        // 默认颜色为黑色
        Arrays.fill(output,-16777216);
        getRGB(sourceImage,0,0,width,height,setA);
        // 构造运算元素
        List<Point2D> srcOp=getSrcOpVector(setA,width,height);
        List<Point2D> seOp=getSeOpVector();
        int srcOp_len=srcOp.size();
        int seOp_len=seOp.size();
        // 向量运算结果
        int index=0;
        int pixel=127;
        boolean[] flag=new boolean[srcOp_len];
        Arrays.fill(flag,true);
        // 腐蚀运算
        for(int i=0;i<srcOp_len;i++){
            for(int j=0;j<seOp_len;j++){
                Point2D res= Point2D.add(srcOp.get(i),seOp.get(j));
                int x=res.getX();
                int y=res.getY();
                if(x<0||x>=width) x=0;
                if(y<0||y>=height) y=0;
                index=y*width+x;
                pixel=(setA[index]>>16)&0xff;
                if(pixel<127){
                    flag[i]=false;
                    break;
                }
            }
        }
        // 处理运算结果
        index=0;
        this.variation=0;
       for(int i=0;i<srcOp_len;i++){
            Point2D res=srcOp.get(i);
            if(flag[i]){
                int x=res.getX();
                int y=res.getY();
                if(x<0||x>=width) x=0;
                if(y<0||y>=height) y=0;
                index=y*width+x;
                output[index]=-1;
                this.variation++;
            }
        }
        this.variation=srcOp.size()-this.variation;
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    public int getVariation() {
        return variation;
    }

    public void setVariation(int variation) {
        this.variation = variation;
    }
}
