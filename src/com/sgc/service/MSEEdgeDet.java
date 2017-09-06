package com.sgc.service;

import com.sgc.photo.AbstractBufferedImageOp;
import com.sgc.photo.BasicOperatorFilter;
import com.sgc.photo.BinaryFilter;
import com.sgc.photo.ErosinFilter;

import java.awt.image.BufferedImage;

/**
 * Created by ChengXX on 2017/3/25.
 */
public class MSEEdgeDet extends AbstractBufferedImageOp {

    private int[][] dir1=new int[][]{{0,0,0},{1,1,1},{0,0,0}};
    private int[][] dir2=new int[][]{{0,1,0},{0,1,0},{0,1,0}};
    private int[][] dir3=new int[][]{{1,0,0},{0,1,0},{0,0,1}};
    private int[][] dir4=new int[][]{{0,0,1},{0,1,0},{1,0,0}};

    private int[][] xDirEdgeKernel=new int[][]{{-1,0,1},{-1,0,1},{-1,0,1}};
    private int[][] yDirEdgeKernel=new int[][]{{1,1,1},{0,0,0},{-1,-1,-1}};

    public BufferedImage prewittEdge(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        // prewitt卷积运算半径
        int rRadius=xDirEdgeKernel.length/2;
        int cRadius=xDirEdgeKernel[0].length/2;
        // 计算梯度阀值
        int index,index2;
        int ncol,nrow;
        int ta,tr;
       int count=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                tr=0;
                for(int subRow=-rRadius;subRow<=rRadius;subRow++){
                    nrow=row+subRow;
                    if(nrow<0||nrow>=height) nrow=row-subRow;
                    for(int subCol=-cRadius;subCol<=cRadius;subCol++){
                        ncol=col+subCol;
                        if(ncol<0||ncol>=width) ncol=col-subCol;
                        index2=nrow*width+ncol;
                        tr+=((inPixels[index2]>>16)&0xff)*xDirEdgeKernel[rRadius+subRow][cRadius+subCol];
                    }
                }
                tr=Math.abs(tr);
                output[index]=tr;
                count+=tr;
            }
        }
        double max=Math.max(count*4.0/(width*height),60);
        System.out.println("max="+max);
        // 根据梯度阀值进行二值化
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                tr=output[index];
                output[index]=tr>max?-1:-16777216;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
//        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.OSTU_THRESHOLD);
//        dest=binaryFilter.filter(dest,null);
        return dest;
    }

    public BufferedImage filter(BufferedImage sourceImage,BufferedImage dest){
        if(null==dest) dest=createCompatibleDestImage(sourceImage,null);
        ErosinFilter erosinFilter=new ErosinFilter();
        erosinFilter.setStructureElements(dir1);
        BufferedImage e1=erosinFilter.filter(sourceImage,null);
        double count1=erosinFilter.getVariation();
        erosinFilter.setStructureElements(dir2);
        BufferedImage e2=erosinFilter.filter(sourceImage,null);
        double count2=erosinFilter.getVariation();
        erosinFilter.setStructureElements(dir3);
        BufferedImage e3=erosinFilter.filter(sourceImage,null);
        double count3=erosinFilter.getVariation();
        erosinFilter.setStructureElements(dir4);
        BufferedImage e4=erosinFilter.filter(sourceImage,null);
        double count4=erosinFilter.getVariation();

        double sum=count1+count2+count3+count4;

        double a1=count1/sum;
        double a2=count2/sum;
        double a3=count3/sum;
        double a4=count4/sum;

        BasicOperatorFilter basicOperatorFilter=new BasicOperatorFilter();
        e1=basicOperatorFilter.scale(e1,a1);
        e2=basicOperatorFilter.scale(e2,a2);
        e3=basicOperatorFilter.scale(e3,a3);
        e4=basicOperatorFilter.scale(e4,a4);

        BufferedImage e=basicOperatorFilter.add(e1,e2);
        e=basicOperatorFilter.add(e,e3);
        e=basicOperatorFilter.add(e,e4);
        e=basicOperatorFilter.normal(e);
        dest=basicOperatorFilter.minus(sourceImage,e);
        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        dest=binaryFilter.filter(dest,null);
        return dest;
    }
}
