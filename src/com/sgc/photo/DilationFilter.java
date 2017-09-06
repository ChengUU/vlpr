package com.sgc.photo;

import com.sgc.geometric.Point2D;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/22.
 */
public class DilationFilter extends AbstractBufferedImageOp {
    public final static int[][] DEFAULT_STRUCTURE_ELEMENT={{1,1,1},{1,1,1},{1,1,1}};
    private int[][] structureElements;// 结构元素
    public DilationFilter(){
        this.structureElements=DEFAULT_STRUCTURE_ELEMENT;
    }

    public DilationFilter(int[][] structureElements){
        this.structureElements=structureElements;
    }

    public int[][] getStructureElements(){return this.structureElements;}

    public void setStructureElements(int[][] structureElements){this.structureElements=structureElements;}

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
        Set<Point2D> opRes=new HashSet<>();
        // 膨胀运算
        for(int i=0;i<srcOp_len;i++){
            for(int j=0;j<seOp_len;j++){
                Point2D res= Point2D.add(srcOp.get(i),seOp.get(j));
                opRes.add(res);
            }
        }
        // 处理运算结果
        int index=0;
        Iterator<Point2D> ite=opRes.iterator();
       while(ite.hasNext()){
            Point2D res=ite.next();
            int x=res.getX();
            int y=res.getY();
           if(x<0||x>=width) x=0;
           if(y<0||y>=height) y=0;
           index=y*width+x;
           output[index]=-1;// white
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    // 获取原始图像向量
    protected List<Point2D> getSrcOpVector(int[] pixel,int width,int height){
        List<Point2D> res=new ArrayList<>();
        int index=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int tr=(pixel[index]>>16)&0xff;
                if(tr>127) res.add(new Point2D(col,row));
            }
        }
        return res;
    }
    // 获取结构元素向量
    protected List<Point2D> getSeOpVector(){
        List<Point2D> res=new ArrayList<>();
        int index=0;
        // 结构元素高和宽


        int seHeight=this.getStructureElements().length;
        int seWidth=this.getStructureElements()[0].length;
        int seh=(int)(seHeight/2.0f);
        int sew=(int)(seWidth/2.0f);
        for(int row=0;row<seHeight;row++){
            for(int col=0;col<seWidth;col++){
                if(structureElements[row][col]!=0)  res.add(new Point2D(col-sew,row-seh));
            }
        }
        return res;
    }
}
