package com.sgc.photo;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by ChengXX on 2017/3/14.
 */
public class StatisticsFilter extends AbstractBufferedImageOp {
    //最大值滤波
    public static final int MAX_FILTER=1;
    //最小值滤波
    public static final int MIN_FILTER=2;
    //最大最小值滤波
    public static final int MIN_MAX_FILTER=4;
    //中值滤波
    public static final int MEADIAN_FILTER=8;
    //中间点滤波
    public static final int MID_POINT_FILTER=16;
    //滤波窗口
    private int kernelSize=3;
    //滤波类型
    private int type=8;
    public StatisticsFilter(){}

    public int getKernelSize(){
        return this.kernelSize;
    }

    public void setKernelSize(int kernelSize){
        this.kernelSize=kernelSize;
    }

    public int getType(){
        return this.type;
    }

    public void setType(int type){
        this.type=type;
    }

    public BufferedImage filter(BufferedImage srcImage,BufferedImage dest){
        int width=srcImage.getWidth();
        int height=srcImage.getHeight();

        if(null==dest)  dest=createCompatibleDestImage(srcImage,null);
        int[] inPixles=new int[width*height];
        int[] outPixles=new int[width*height];
        getRGB(srcImage,0,0,width,height,inPixles);
        int row2=kernelSize/2;
        int col2=kernelSize/2;
        int index=0;
        int index2=0;
        float total=kernelSize*kernelSize;
        int[][] matrix=new int[3][kernelSize*kernelSize];
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int count=0;
                for(int row=-row2;row<=row2;row++){
                    int rowoffset=y+row;
                    if(rowoffset<0||rowoffset>=height){
                        rowoffset=y-row;
                    }
                    for(int col=-col2;col<=col2;col++){
                        int coloffset=x+col;
                        if(coloffset<0||coloffset>=width){
                            coloffset=x-col;
                        }
                        index2=rowoffset*width+coloffset;
                        //按获取各像素点RGB数组
                        matrix[0][count]=(inPixles[index2]>>16)&0xff;
                        matrix[1][count]=(inPixles[index2]>>8)&0xff;
                        matrix[2][count]=inPixles[index2]&0xff;
                        count++;
                    }
                }
                int[] rgb=performFilter(matrix);
                int ia=0xff;
                int ir=rgb[0];
                int ig=rgb[1];
                int ib=rgb[2];
                outPixles[index++]=(ia<<24)|(ir)<<16|(ig)<<8|ib;
            }
        }
        setRGB(dest,0,0,width,height,outPixles);
        return dest;
    }

    private int[] performFilter(int[][] matrix){
        int[] rgb=new int[3];
        int[] trs=matrix[0];
        int[] tgs=matrix[1];
        int[] tbs=matrix[2];
        //default order asc
        Arrays.sort(trs);
        Arrays.sort(tgs);
        Arrays.sort(tbs);
        int count=kernelSize*kernelSize;
        switch(this.type){
            case MEADIAN_FILTER:rgb[0]=trs[count/2];rgb[1]=tgs[count/2];rgb[2]=tbs[count/2];break;
            case MIN_MAX_FILTER:rgb[0]=trs[count-1]-trs[0];rgb[1]=tgs[count-1]-tgs[0];rgb[2]=tbs[count-1]-tbs[0];break;
            case MAX_FILTER:rgb[0]=trs[count-1];rgb[1]=tgs[count-1];rgb[2]=tbs[count-1];break;
            case MIN_FILTER:rgb[0]=trs[0];rgb[1]=tgs[0];rgb[2]=tbs[0];break;
            case MID_POINT_FILTER:rgb[0]=(trs[count-1]+trs[0])/2;rgb[1]=(tgs[count-1]+tgs[0])/2;rgb[2]=(tbs[count-1]+tbs[0])/2;break;
        }
        return rgb;
    }

}
