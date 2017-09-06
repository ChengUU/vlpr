package com.sgc.photo;

import com.sgc.geometric.ConnRegion;
import com.sgc.res.GrayImgOneOrderDiffRes;
import com.sgc.service.Projection;

import java.awt.image.BufferedImage;
import java.util.Arrays;


/**
 * Created by ChengXX on 2017/4/1.
 */
public class BorderDetFilter extends AbstractBufferedImageOp {
    private  GrayImgOneOrderDiffRes grayImgOneOrderDiffRes;
    private double oneOrderDiffMean;
    private int leftBorder;
    private int rightBorder;

    private int blank_left;
    private int blank_right;


    private int topBorder;
    private int bottomBorder;
    // 高通滤波去除间隔符及其类似杂波
    public BufferedImage dealSPCCharacter(BufferedImage image){
        // 获取图像尺寸及数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[height*width];
        getRGB(image,0,0,width,height,inPixels);
        // 获取投影数据
        int[][] hv=Projection.getProjectionData(inPixels,width,height);
        // 间隔标记--表示间隔已经出现
        boolean flag = true;
        // 投影阀值
        int threshold = 0;
        // 点标记
        int[] mark = new int[width];
        Arrays.fill(mark, 0);
        for (int col = 0; col < width; col++) {
            if (!flag && threshold == hv[1][col]) {
                flag = true;
                mark[col] = 1;
            } else if (flag && threshold != hv[1][col]) {
                flag = false;
                mark[col] = 2;
            }
        }
        // 滤波阀值
        double filterThreshold=height*25.0/100;
        // 计算非零线段间的最大投影值
        int max=Integer.MIN_VALUE;
        int noiseWidth=0;
        int[] noises=null;
        for(int col=0;col<width;col++){
            if(2!=mark[col]) continue;
            max=Integer.MIN_VALUE;
            for(int i=col;i<width;i++){
                if(max<hv[1][i]) max=hv[1][i];
                if(1!=mark[i]) continue;
                if(max>filterThreshold) break;
                noiseWidth=i-col+1;
                System.out.println("噪点宽度:"+noiseWidth);
                noises=new int[noiseWidth*height];
                Arrays.fill(noises,-16777216);
                setRGB(image,col,0,noiseWidth,height,noises);
            }
        }
        return image;
    }
    public GrayImgOneOrderDiffRes computeOneOrderDiff(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] oneOrderDiff=new int[width*height];
        Arrays.fill(oneOrderDiff,0);
        int index1,index2,tr1,tr2;
        int sum=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index1=row*width+col;
                tr1=(inPixels[index1]>>16)&0xff;
                index2=(col+1>=width)?index1:index1+1;
                tr2=(inPixels[index2]>>16)&0xff;
                oneOrderDiff[index1]=Math.abs(tr1-tr2);
                sum+=oneOrderDiff[index1];
            }
        }
        this.oneOrderDiffMean=sum/(width*height);
        this.grayImgOneOrderDiffRes=new GrayImgOneOrderDiffRes(width,height,oneOrderDiff);
        return this.grayImgOneOrderDiffRes;
    }
    public void computeVerBorderTest(BufferedImage image){
        // 图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 获取投影数据
        int[][] hv=Projection.getProjectionData(inPixels,width,height);
        // 获取字符宽度及其间距
        double wMean=PhotoExcision.computeWMean(image);
        double spcMean=PhotoExcision.computeSPCMean(image);
        // 比例自动化
        double r1=(57-spcMean)/(wMean-spcMean);
        double r2=1-r1;
        System.out.println("字符平均宽度:" + wMean + "spcMean:" + spcMean);
        int plate_width = (int) (wMean / 45 * 340*r1 + spcMean / 12 * 90*r2 + 0.5);
        System.out.println("字符区域宽度:"+plate_width);
        // 左边界确定
        int leftLimit=width-plate_width;
        int[] g=new int[leftLimit];
        Arrays.fill(g,0);
        for(int col=0;col<leftLimit;col++) {
            for (int i = col; i < col+plate_width; i++) {
                g[col] += hv[1][i];
            }
        }
        // 计算投影跳变
        int max=Integer.MIN_VALUE;
        int leftIndex=0;
        for(int col=0;col<leftLimit;col++){
            if(max<g[col]){
                leftIndex=col;
                max=g[col];
            }

        }
        this.leftBorder=leftIndex;
        this.rightBorder=leftIndex+plate_width;
    }

    public BufferedImage dealVerBorder(BufferedImage image){
        int height=image.getHeight();
        // 计算左右边界
        computeVerBorderTest(image);
        int outw=this.rightBorder-this.leftBorder+1;
        int[] outPixels=new int[height*outw];
        // 进行裁剪
        getRGB(image,this.leftBorder,0,outw,height,outPixels);
        BufferedImage dest=new BufferedImage(outw,height,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,outw,height,outPixels);
        return dest;

    }

    public void computeHorBorderTest(BufferedImage image){
        int width= image.getWidth();
        int height=image.getHeight();
        // 图像数据
        int[] inPixels=new int[height*width];
        getRGB(image,0,0,width,height,inPixels);
        int[] nCount=new int[height];
        boolean[] truth=new boolean[height];
        Arrays.fill(nCount,0);
        Arrays.fill(truth,true);
        // 跳变标记
        boolean flag=true;
        // 设定跳变阀值
        int threshold=255;
        // 中间行像素坐标及该点的像素值
        int index;
        int pixel;
        for(int row=0;row<height;row++) {
            for (int i = 0; i < width; i++) {
                index = row * width + i;
                pixel = (inPixels[index] >> 16) & 0xff;
                if (!flag && pixel == threshold){
                    flag = true;
                    nCount[row]++;
                }
                else if (flag && pixel != threshold) {
                    flag = false;
                    nCount[row]++;
                }
            }
            if(nCount[row]<T) truth[row]=false;
        }
        // 打印跳变判断值
//        for(int row=0;row<height;row++){
//            System.out.println(String.format("coe[%d]:",row)+truth[row]);
//        }
        // 初始化上下边界值
        this.topBorder=-1;
        this.bottomBorder=height;
        // 确定上下边界
        int middleRow=(height-1)/2;
        System.out.println("middleRow="+middleRow);
        for(int row=0;row<=middleRow;row++) {
            if(-1==this.topBorder&&!truth[middleRow-row]){
                this.topBorder=middleRow-row;
                break;
            }
        }
        for(int row=middleRow;row<height;row++) {
            if(height==this.bottomBorder&&!truth[row]){
                this.bottomBorder=row;
                break;
            }
        }
        // 边界状态检测
        if(height==this.bottomBorder) this.bottomBorder=height-1;
        if(-1==this.topBorder) this.topBorder=0;
        System.out.println("topBorder:"+this.topBorder+"bottomBorder:"+this.bottomBorder);
    }

    public BufferedImage dealHorBorder(BufferedImage image){
//        computeHorBorder(image);
        computeHorBorderTest(image);
        int width=image.getWidth();
        int outh=this.bottomBorder-this.topBorder;
        int[] outPixels=new int[width*outh];
        // 进行裁剪
        getRGB(image,0,this.topBorder,width,outh,outPixels);
        BufferedImage dest=new BufferedImage(width,outh,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,width,outh,outPixels);
        return dest;

    }

    public BufferedImage dealAllBorder(BufferedImage image){
        // 根据先验知识进行最后处理
        int width=image.getWidth();
        int height=image.getHeight();

        int x=(int)(width*VER_LINE_RATE)+1;
        int y=(int)(height*HOR_LINE_RATE)+1;

        int outw=(int)(width*(1-2*VER_LINE_RATE))+1;
        int outh=(int)(height*(1-2*HOR_LINE_RATE))+1;

        int[] outPixels=new int[outh*outw];
        getRGB(image,x,y,outw,outh,outPixels);
        BufferedImage dest=new BufferedImage(outw,outh,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,outw,outh,outPixels);

        return dest;

    }

    private void computeVerBlank(BufferedImage image){
        // 图像尺寸
        int width=image.getWidth();
        int height=image.getHeight();
        // 图像像素数据
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 获取投影数据
        int[][] hv=Projection.getProjectionData(inPixels,width,height);
        // 寻找像素存在的位置(左右)
        int left,right;
        left=-1;right=width;
        double threshold=0;
        for(int col=0;col<width;col++){
            if(-1==left&&threshold<hv[1][col]) left=col;
            if(-1!=left) break;
        }
        for(int col=width-1;col>=0;col--) {
            if(width==right&&threshold<hv[1][col]) right=col;
            if(width!=right) break;
        }

        System.out.println("left="+left+"right="+right);
        if(left>=right) throw new IllegalArgumentException("com.sgc.photo.BorderDelFilter dealBlank():illegal image......");
        this.blank_left=left;
        this.blank_right=right;
    }

    public BufferedImage dealVerBlank(BufferedImage image,boolean flag){
        int height=image.getHeight();
        BufferedImage temp=image;
        if(!flag){
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        temp=binaryFilter.filter(image,null);}
        computeVerBlank(temp);
        int outw=this.blank_right-this.blank_left+1;
        int[] output=new int[outw*height];
        getRGB(image,this.blank_left,0,outw,height,output);
        BufferedImage dest=new BufferedImage(outw,height,BufferedImage.TYPE_INT_RGB);
        setRGB(dest,0,0,outw,height,output);
        return dest;
    }

    public GrayImgOneOrderDiffRes getGrayImgOneOrderDiffRes() {
        return grayImgOneOrderDiffRes;
    }

    public void setGrayImgOneOrderDiffRes(GrayImgOneOrderDiffRes grayImgOneOrderDiffRes) {
        this.grayImgOneOrderDiffRes = grayImgOneOrderDiffRes;
    }

    public double getOneOrderDiffMean() {
        return oneOrderDiffMean;
    }

    public void setOneOrderDiffMean(double oneOrderDiffMean) {
        this.oneOrderDiffMean = oneOrderDiffMean;
    }

    public int getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(int leftBorder) {
        this.leftBorder = leftBorder;
    }

    public int getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(int rightBorder) {
        this.rightBorder = rightBorder;
    }

    public int getTopBorder() {
        return topBorder;
    }

    public void setTopBorder(int topBorder) {
        this.topBorder = topBorder;
    }

    public int getBottomBorder() {
        return bottomBorder;
    }

    public void setBottomBorder(int bottomBorder) {
        this.bottomBorder = bottomBorder;
    }

    public int getBlank_left() {
        return blank_left;
    }

    public void setBlank_left(int blank_left) {
        this.blank_left = blank_left;
    }

    public int getBlank_right() {
        return blank_right;
    }

    public void setBlank_right(int blank_right) {
        this.blank_right = blank_right;
    }

    public final  static  double VER_LINE_RATE=0.0176;
    public final static double HOR_LINE_RATE=0.1340;

    public static final int T=13;
}
