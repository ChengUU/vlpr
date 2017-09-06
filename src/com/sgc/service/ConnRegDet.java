package com.sgc.service;

import com.sgc.geometric.ConnRegion;
import com.sgc.geometric.Point2D;
import com.sgc.photo.AbstractBufferedImageOp;
import com.sgc.photo.BasicOperatorFilter;
import com.sgc.photo.CloseFilter;
import com.sgc.photo.DilationFilter;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by ChengXX on 2017/3/24.
 */
public class ConnRegDet extends AbstractBufferedImageOp {
    public static final int[][] dir=new int[][]{{1,-1,0,0},{0,0,1,-1}};
    public static final int DIR_LEN=4;


    public List<ConnRegion> findConnRegs(BufferedImage image){
        if(null==image) throw new IllegalArgumentException("The object image must be a real image......");
        List<ConnRegion> connRegs=new ArrayList<>();
        int width=image.getWidth();
        int height=image.getHeight();

        int[] inPixels=new int[width*height];
        boolean[] flag=new boolean[width*height];
        getRGB(image,0,0,width,height,inPixels);
        Arrays.fill(flag,false);
        Queue<Point2D> queue=new LinkedList<>();
        int index=0;
        int minX=Integer.MAX_VALUE;
        int minY=Integer.MAX_VALUE;
        int maxX=Integer.MIN_VALUE;
        int maxY=Integer.MIN_VALUE;
        int nextX=0;
        int nextY=0;
        int cx=0;
        int cy=0;
        int pix1=0;
        int pix2=0;
        Point2D current=null;
        int area=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                if(!flag[index]){
                    queue.add(new Point2D(col,row));
                    flag[index]=true;
                    // 初始化连通区域属性参数
                    minX=Integer.MAX_VALUE;
                    minY=Integer.MAX_VALUE;
                    maxX=Integer.MIN_VALUE;
                    maxY=Integer.MIN_VALUE;
                    area=0;
                    // 进行广度搜索
                    while(!queue.isEmpty()){
                        // 访问队头元素
                        area++;
                        current=queue.poll();
                        cx=current.getX();
                        cy=current.getY();
                        index=cy*width+cx;
                        pix1=inPixels[index];
                        minX=Math.min(minX,cx);
                        minY=Math.min(minY,cy);
                        maxX=Math.max(maxX,cx);
                        maxY=Math.max(maxY,cy);
                        for(int i=0;i<DIR_LEN;i++){
                            nextX=cx+dir[0][i];
                            nextY=cy+dir[1][i];
                            if(nextX<0||nextX>=width) continue;
                            if(nextY<0||nextY>=height) continue;
                            index=nextY*width+nextX;
                            pix2=inPixels[index];
                            if(!flag[index] && -1==pix1&&pix2==pix1){
                                flag[index]=true;
                                queue.add(new Point2D(nextX,nextY));
                            }
                        }
                    }
                    // 跳过孤立点
                    if(1==area) continue;
                    // 搜索结束-保存结果
                    area=Math.abs(maxX-minX+1)*Math.abs(maxY-minY+1);
                    connRegs.add(new ConnRegion(minX,minY,Math.abs(maxX-minX+1),Math.abs(maxY-minY+1),area));
                }
            }
        }
        return connRegs;
    }

    public List<ConnRegion> findConnRegs(BufferedImage[] images,BufferedImage imgB){
        List<ConnRegion> connRegs=new ArrayList<>();
        int len=images.length;
        BufferedImage destImage;
        for(int i=0;i<len;i++){
            // 进行与运算
            BasicOperatorFilter basicOperatorFilter=new BasicOperatorFilter();
            destImage=basicOperatorFilter.and(imgB,images[i]);
            // 进行形态学闭运算--形成闭合区域
            int[][] se=new int[][]{{1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1}};
            CloseFilter closeFilter=new CloseFilter();
            closeFilter.setStructureElements(se);
            destImage=closeFilter.filter(destImage,null);

            // 进行膨胀运算--去除差异较大的小洞
            DilationFilter dilationFilter=new DilationFilter();
            int[][] A=new int[][]{{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1}};
            dilationFilter.setStructureElements(A);
            destImage=dilationFilter.filter(destImage,null);
            // 连通区域探测
            List<ConnRegion> temp=findConnRegs(destImage);
            // 连通区域物理特征判断
            ConnRegion connRegion=null;
            Collections.sort(temp);
            Iterator<ConnRegion> ite=temp.iterator();
            while(ite.hasNext()) {
                connRegion = ite.next();
                double wh=connRegion.getWidth()/connRegion.getHeight();
                double e = 22 * connRegion.getArea() / (connRegion.getPremeter() * connRegion.getPremeter());
                if (e < 0.9 || e > 1.25 || connRegion.getArea() < 1200) ite.remove();
            }
            connRegs.addAll(temp);
        }
        System.out.println(connRegs);
        return connRegs;
    }

    public BufferedImage horizontalClusterSearch(BufferedImage image,int cw,double coe){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        double threshold=coe*cw;
        int index,index2,ncol,jEnd;
        int num=0;
        for(int row=0;row<height;row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                if (-1 != inPixels[index]) continue;
                ncol = col + 2 * cw;
                if (ncol >= width) break;
                jEnd = col - 1;
                num = 0;
                for (int subCol = col; subCol <= ncol; subCol++) {
                    index2 = row * width + subCol;
                    if (-1 != inPixels[index2]) continue;
                    num++;
                    jEnd = subCol;
                }
                if (num > threshold)
                    for (int subCol = col; subCol <= jEnd; subCol++) {
                        index2 = row * width + subCol;
                        output[index2] = -1;
                    }
                col += 2 * cw;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
  public BufferedImage show(BufferedImage image, List<ConnRegion> connRegs){
        BufferedImage dest=createCompatibleDestImage(image,null);
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        Arrays.fill(inPixels,-16777216);
        ConnRegion connRegion;
        int cx,cy,cw,ch;
        Iterator<ConnRegion> ite=connRegs.iterator();
        while(ite.hasNext()){
            Arrays.fill(inPixels,-16777216);
            connRegion=ite.next();
            cx=connRegion.getX();
            cy=connRegion.getY();
            cw=connRegion.getWidth();
            ch=connRegion.getHeight();
            getRGB(image,cx,cy,cw,ch,inPixels);
            setRGB(dest,cx,cy,cw,ch,inPixels);
        }
        return dest;
    }
}
