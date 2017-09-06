package com.sgc.test;

import com.sgc.geometric.ConnRegion;
import com.sgc.res.*;
import com.sgc.photo.*;
import com.sgc.service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.sgc.photo.GrayFilter;
import com.sgc.util.Util;


/**
 * Created by ChengXX on 2017/3/11.
 */
public class ImagePanel extends JPanel{
    public static int CHARACTER_WIDTH=45;
    public  static int CHARACTER_HEIGHT=90;

    public final static int W_MIN=45;
    public final static int W_MAX=185;
    public final static int H_MIN=9;
    public final static int H_MAX=60;

    private BufferedImage sourceImage;
    private BufferedImage middleSection;
    private BufferedImage destImage;

    private java.util.List<ExcisionUnit> excisions;

    public void rgColorSpcClusTest(){
        PictureEnhanced pictureEnhanced=new PictureEnhanced();
         destImage=pictureEnhanced.enhance(sourceImage);
         middleSection=destImage;
          //边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        middleSection=mseEdgeDet.prewittEdge(destImage);
        destImage=middleSection;
         //粗步去噪
        LicensePlateDenoise licensePlateDenoise=new LicensePlateDenoise();
        middleSection=licensePlateDenoise.preDenoise(middleSection);
        // 精确去噪
        middleSection=licensePlateDenoise.denoise(middleSection);
         //灰度能量聚类,去除为车牌边缘图
        GrayEnergyCluster grayEnergyCluster=new GrayEnergyCluster(5,11);
        middleSection=grayEnergyCluster.cluster(middleSection,destImage);
        // 图像二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        destImage=binaryFilter.filter(middleSection,null);
        middleSection=destImage;
         //水平聚类线性探测法
        ConnRegDet connRegDet=new ConnRegDet();
        middleSection=connRegDet.horizontalClusterSearch(destImage,12,0.60);
         //形态学操作
        int[][] C=new int[][]{ {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1}};
       // 开运算
        CloseFilter closeFilter=new CloseFilter();
        closeFilter.setStructureElements(C);
        middleSection=closeFilter.filter(middleSection,null);
        destImage=middleSection;
        middleSection=licensePlateDenoise.denoiseConn(middleSection);
        List<ConnRegion> list=connRegDet.findConnRegs(middleSection);
        System.out.println(list);
        // 显示候选区域一
        middleSection=connRegDet.show(sourceImage,list);
        // 灰度化过滤器
        GrayFilter grayFilter=new GrayFilter();
        // 直方图均衡化滤波器
        HistogramFIlter histogramFIlter=new HistogramFIlter();
        // 投影滤波器
        Projection projection=new Projection();
        List<List<Double>> pi=new ArrayList<>();
        double hi;
        double varSum=0;
        Iterator<ConnRegion> ite=list.iterator();
        while(ite.hasNext()) {
            ConnRegion connRegion = ite.next();
//            System.out.println(connRegion);
            ObjRegDet objRegDet = new ObjRegDet();
            BufferedImage sample = objRegDet.getObjReg(sourceImage, connRegion);
            BufferedImage gray = grayFilter.filter(sample, null);
            List<Double> p = new ArrayList<>();
            double[] pVal=new double[5];
            int width = connRegion.getWidth();
            int height = connRegion.getHeight();
            // 候选区域长宽比
            if (width >= W_MIN && width <= W_MAX && height >= H_MIN && height <= H_MAX) hi = 1.0 * width / height;
            else hi = 0;
            if (hi >= 2 && hi <= 6.5) pVal[0] = 1.0 / (1 + (hi - 4) * (hi - 4));
            else {
                ite.remove();
                System.out.println(0);
                continue;
            }
            // 区域二值图像水平跳变数
            pVal[1] = histogramFIlter.jumpLine(gray);
            if(0==pVal[1]) {
                ite.remove();
                System.out.println(1);
                continue;
            }
            // 候选区域字符所占比例
            pVal[2]=binaryFilter.occupancy(sample);
            if(pVal[2]>=0.1&&pVal[2]<=0.8) pVal[2]=1/(1+100*(pVal[2]-0.35)*(pVal[2]-0.35));
            else {
                ite.remove();
//                System.out.println(2);
                continue;
            }
            // 垂直投影峰谷跳变
            pVal[4]=projection.troughJumpCount(gray);
            if( pVal[4]>=3&& pVal[4]<=30)  pVal[4]=100.0/(100+(( pVal[4]-8)*( pVal[4]-8))+( pVal[4]-20)*( pVal[4]-20));
            else {
                ite.remove();
//                System.out.println(4 + "=" + pVal[4]);
                continue;
            }
            // 候选区域灰度方差
            pVal[3]=grayFilter.computeVariance(sample);
            varSum+=pVal[3];
            for(double value:pVal) p.add(value);
            pi.add(p);
        }
        // 显示候选区域二
        middleSection=connRegDet.show(sourceImage,list);
        // 更新灰度方差
        int size= pi.size();
        double varMean=varSum/size;
//        System.out.println("varMean="+varMean);
        int i=0;
        Iterator<List<Double>> pIte= pi.iterator();
        while(pIte.hasNext()) {
            List<Double> ptmp = pIte.next();
            double p3 = ptmp.get(3);
            if(p3<varMean)
                p3 = 1 - p3 / varMean;
            else if(p3>varMean) p3=0;
            ptmp.set(3, p3);
            i++;
        }
        System.out.println("i="+i);
        size=list.size();
        List<ConnRegion> objList=new ArrayList<>();
        pIte=pi.iterator();
        i=0;
        while(pIte.hasNext()) {
            List<Double> ptmp = pIte.next();
            Double[] pVal=ptmp.toArray(new Double[5]);
            System.out.println("pVal[0]="+pVal[0]+"pVal[1]="+pVal[1]+"pVal[2]="+pVal[2]+"pVal[3]="+pVal[3]+"pVal[4]="+pVal[4]);
            double coe=0.3*pVal[0]+0.2*pVal[1]+0.15*pVal[2]+0.2*pVal[3]+0.15*pVal[4];
            if(coe>=0.5) {
                ConnRegion connRegion=list.get(i);
                double width=connRegion.getWidth();
                double height=connRegion.getHeight();
                double wh=width/height;
                int deltaY=0;
//                System.out.print("wh="+wh);
                if(wh>=3.2){
                    deltaY=(int)(height*0.2);
                    height=height*1.4;
                    int y=connRegion.getY();
                    y=y-deltaY;
                    connRegion.setHeight((int)height);
                    connRegion.setY(y);
                }
                objList.add(list.get(i));
            }
            System.out.println("综合隶属度="+coe);
            i++;
        }
        // 显示候选区域三
        middleSection=connRegDet.show(sourceImage,objList);
        // 目标图像获取
        ObjRegDet objRegDet=new ObjRegDet();
        ite=objList.iterator();
        while(ite.hasNext()){
            ConnRegion connRegion=ite.next();
            middleSection=objRegDet.getObjReg(sourceImage,connRegion);
            check(middleSection);
        }

    }

    public static double getBandwidth(List<List<Double>> points,double t){
        List<Double> c=new ArrayList<>();
        int d=points.get(0).size();
        int N=points.size();
        for(int i=0;i<d;i++){
            c.add(0.0);
        }
        for(int i=0;i<d;i++){
            for(int j=0;j<N;j++){
                double di=c.get(i);
                di+=points.get(j).get(i);
                c.set(i,di);
            }
        }
        for(int i=0;i<d;i++){
            double di=c.get(i);
            c.set(i,di/N);
        }
        double[] D=new double[N];
        for(int i=0;i<N;i++){
            D[i]= Util.euclidean_distance(points.get(i),c);
        }
        double _D=0;
        for(int i=0;i<N;i++){
            _D=_D+D[i];
        }
        _D/=N;
        return t*_D;
    }


    public ImagePanel(){
        setBackground(Color.WHITE);
    }
    protected void paintComponent(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        g2d.fillRect(0,0,this.getWidth(),this.getHeight());
        if(null!=this.sourceImage){
            g2d.drawImage(this.sourceImage,0,0,this.sourceImage.getWidth(),this.sourceImage.getHeight(),null);
            if(null!=middleSection){
                g2d.drawImage(this.middleSection,this.sourceImage.getWidth()+10,0,this.middleSection.getWidth(),this.middleSection.getHeight(),null);
                if(null!=this.destImage){
                    g2d.drawImage(this.destImage,this.sourceImage.getWidth()+10+this.middleSection.getWidth()+10,0,this.destImage.getWidth(),this.destImage.getHeight(),null);
                }
            }
        }
        int width,height;
        if(null!=excisions){
            Iterator<ExcisionUnit> ite=excisions.iterator();
            int i=0;
           while (ite.hasNext()){
                ExcisionUnit excisionUnit=ite.next();
                if(excisionUnit==null) continue;
                width=excisionUnit.getWidth();
                height=excisionUnit.getHeight();
                BufferedImage temp=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                AbstractBufferedImageOp.setRGB(temp,0,0,width,height,excisionUnit.getPixels());
                g2d.drawImage(temp,i*80,400,null);
                i++;
            }
        }
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(BufferedImage sourceImage) {
        this.sourceImage = sourceImage;
    }

    public BufferedImage getDestImage() {
        return destImage;
    }
    public void process() {
//        PictureSegmentation picseg=new PictureSegmentation(0.0625);
//        PictureSegmentation picseg=new PictureSegmentation(0.1328125);
//        PictureSegmentation picseg=new PictureSegmentation(0.19921875);
//        PictureSegmentation picseg=new PictureSegmentation(0.23242187);
//        PictureSegmentation picseg=new PictureSegmentation(0.17,0.34,0.0055);
//        PictureSegmentation picseg=new PictureSegmentation(0.17,0.68,0.011);
//        PictureSegmentation picseg=new PictureSegmentation(0.34,0.34,0.011);
//        PictureSegmentation picseg=new PictureSegmentation(0.17,0.68,0.0055);
//        PictureSegmentation picseg=new PictureSegmentation(0.34,0.35,0.0055);
//        PictureSegmentation picseg=new PictureSegmentation(0.29492175);
//        PictureSegmentation picseg=new PictureSegmentation(0.302246);
//        PictureSegmentation picseg=new PictureSegmentation(0.30957025);
//        PictureSegmentation picseg=new PictureSegmentation(0.32421875);
//        PictureSegmentation picseg=new PictureSegmentation(0.3828125);
//        PictureSegmentation picseg=new PictureSegmentation(0.44140625);
//        PictureSegmentation picseg=new PictureSegmentation(0.47070313);
//        PictureSegmentation picseg=new PictureSegmentation(0.5);
//        PictureSegmentation picseg=new PictureSegmentation(0.6);
//        PictureSegmentation picseg=new PictureSegmentation(0.6640625);
//        PictureSegmentation picseg=new PictureSegmentation(0.7);
//        PictureSegmentation picseg=new PictureSegmentation(0.8);
//        PictureSegmentation picseg=new PictureSegmentation(0.9);
//        PictureSegmentation picseg=new PictureSegmentation(1.0);
//        PictureSegmentation picseg=new PictureSegmentation(1.1);

//        StatisticsFilter statisticsFilter=new StatisticsFilter();
//        statisticsFilter.setType(StatisticsFilter.MEADIAN_FILTER);
//        sourceImage=statisticsFilter.filter(sourceImage,null);
//
//        destImage=picseg.filter(sourceImage,destImage);

        //图像灰度化
        //GrayFilter grayFilterOp=new GrayFilter();
        //sourceImage=grayFilterOp.filter(sourceImage,null);
        //空白图像过滤
        //PixleStaticFilter filter=new PixleStaticFilter();
        //destImage=filter.filter(sourceImage,null);
        //图像二值化
        //BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        //destImage=binaryFilter.filter(sourceImage,null);
    }

    public void process(double[] bandwidths,int[] segs){
        PictureSegmentation picseg=new PictureSegmentation(bandwidths,segs);
        StatisticsFilter statisticsFilter=new StatisticsFilter();
        statisticsFilter.setType(StatisticsFilter.MEADIAN_FILTER);
        sourceImage=statisticsFilter.filter(sourceImage,null);
        destImage=picseg.filter(sourceImage,destImage);
    }

    public void drawGrayHistogram(){
        // 彩色图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        sourceImage=grayFilter.filter(sourceImage,null);
        // 灰度图像直方图均衡化
        GrayHistogramEFilter histogramEFilter=new GrayHistogramEFilter();
        sourceImage=histogramEFilter.filter(sourceImage,null);
         // 灰度图像直方图数据获取
        HistogramFIlter histogramFIlter=new HistogramFIlter();
        destImage=histogramFIlter.filter(sourceImage,null);
    }
    public void connRegTest(){
        // 高斯模糊处理——去除图像细节,保留图像主要特征
        GaussianBlurFilter gaussianBlurFilter=new GaussianBlurFilter();
        destImage=gaussianBlurFilter.filter(sourceImage,null);
        // 彩色图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        destImage=grayFilter.filter(destImage,null);
        // 灰度图像直方图均衡
        GrayHistogramEFilter grayHistogramEFilter=new GrayHistogramEFilter();
        destImage=grayHistogramEFilter.filter(destImage,null);

         //除噪（中值滤波）
        StatisticsFilter statisticsFilter=new StatisticsFilter();
        statisticsFilter.setType(StatisticsFilter.MEADIAN_FILTER);
        middleSection=statisticsFilter.filter(destImage,null);
        // 灰度膨胀与腐蚀-测试
        // 结构元素
//        int[][] se=new int[][]{{1,2,3},{4,5,6},{7,8,9}};
//        int[][] se=new int[][] {{1,2,3,4,3,2,1},
//                {2,3,4,5,4,3,2},
//                {3,4,5,6,5,4,3},
//                {4,5,6,6,6,5,4},
//                {3,4,5,6,5,4,3},
//                {2,3,4,5,4,3,2},
//                {1,2,3,4,3,2,1}
//        };

//        int[][] se=new int[][] {{0,0,0,0,0,0,0,0,0},
//                {0,0,0,4,4,4,0,0,0},
//                {0,0,4,5,6,5,4,0,0},
//                {0,4,5,6,7,6,5,4,0},
//                {0,4,6,7,7,7,6,4,0},
//                {0,4,5,6,7,6,5,4,0},
//                {0,0,4,5,6,5,4,0,0},
//                {0,0,0,4,4,4,0,0,0},
//                {0,0,0,0,0,0,0,0,0}
//        };
//        int[][] se=new int[][] {{1,2,3,4,5,6,5,4,3,2,1},
//                                {2,3,4,5,6,7,6,5,4,3,2},
//                                {3,4,5,6,7,8,7,6,5,4,3},
//                                {4,5,6,7,8,9,8,7,6,5,4},
//                                {5,6,7,8,9,10,9,8,7,6,5},
//                                {6,7,8,9,10,10,10,9,8,7,6},
//                                {5,6,7,8,9,10,9,8,7,6,5},
//                                {4,5,6,7,8,9,8,7,6,5,4},
//                                {3,4,5,6,7,8,7,6,5,4,3},
//                                {2,3,4,5,6,7,6,5,4,3,2},
//                                {1,2,3,4,5,6,5,4,3,2,1}};
        int[][] se=new int[][] {{0,0,0,0,0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                                {7,8,9,10,11,12,12,12,11,10,9,8,7},
                                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0,0,0,0,0}};
//        // 灰度膨胀
//        GrayDilationFilter grayDilationFilter=new GrayDilationFilter();
//        grayDilationFilter.setStructureElements(se);
//        middleSection=grayDilationFilter.filter(destImage,null);
//        // 灰度腐蚀
//        GrayErosinFilter grayErosinFilter=new GrayErosinFilter();
//        grayErosinFilter.setStructureElements(se);
//        destImage=grayErosinFilter.filter(destImage,null);

        // 灰度高帽低帽变换-测试
        // 高帽变换
        HatFilter hatFilter=new HatFilter();
        middleSection=hatFilter.onTopHat(middleSection,se);
        // 二值化处理
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        destImage=binaryFilter.filter(middleSection,null);


//        // 形态学算子边缘检测
//        // 3*3菱形结构元素
//        int[][] b=new int[][]{{0,1,0},
//                              {1,1,1},
//                              {0,1,0}};
//        // 5*5方形结构元素
//        int[][] B=new int[][]{{1,1,1,1,1},
//                {1,1,1,1,1},
//                {1,1,1,1,1},
//                {1,1,1,1,1},
//                {1,1,1,1,1}
//        };
//        EdgeDet edgeDet=new EdgeDet();
//        edgeDet.setA(b);
//        edgeDet.setB(B);
//        destImage=edgeDet.filter(destImage,null);
        // 测试 多结构元素边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        destImage=mseEdgeDet.filter(destImage,null);


        // 填充区域连通区域
        // 使用较大的长方形做闭运算
        int[][] A=new int[][]{{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                              {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                              {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
        CloseFilter closeFilter=new CloseFilter();
        closeFilter.setStructureElements(A);
        middleSection=closeFilter.filter(destImage,null);
        // 较小圆形结构
        int[][] C=new int[][]{{0,0,0,0,0,0,0},
                {0,0,1,1,1,0,0},
                {0,1,1,1,1,1,0},
                {1,1,1,1,1,1,1},
                {0,1,1,1,1,1,0},
                {0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0}};
//        int[][] C=new int[][]{{0,0,0,0,0},
//                {0,1,1,1,0},
//                {1,1,1,1,1},
//                {0,1,1,1,0},
//                {0,0,0,0,0}};
        // 腐蚀运算-去除边界噪点
        ErosinFilter erosinFilter=new ErosinFilter();
        erosinFilter.setStructureElements(C);
        middleSection=erosinFilter.filter(middleSection,null);
        // 使用小的圆形结构进行开运算
        OpenFilter openFilter=new OpenFilter();
        openFilter.setStructureElements(C);
        middleSection=openFilter.filter(middleSection,null);
        // 使用较小圆形结构进行膨胀运算
        DilationFilter dilationFilter=new DilationFilter();
        dilationFilter.setStructureElements(C);
        destImage=dilationFilter.filter(middleSection,null);



//        int[][] connse=new int[][]{{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
//                                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
//                                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
//        ConnCompFilter connCompFilter=new ConnCompFilter();
//        connCompFilter.setSe(connse);
//        destImage=connCompFilter.filter(destImage,null);

        ConnRegDet connRegionDet=new ConnRegDet();
        List<ConnRegion> connRegs=connRegionDet.findConnRegs(destImage);
        ConnRegion connRegion=null;
        Iterator<ConnRegion> ite=connRegs.iterator();
        ObjRegDet objRegDet=new ObjRegDet();
        while(ite.hasNext()){
            connRegion=ite.next();
            double wh=1.0*connRegion.getWidth()/connRegion.getHeight();
//            double e=22*connRegion.getArea()/(connRegion.getPremeter()*connRegion.getPremeter());
            if(wh>=2.0&&wh<=4.5){
                middleSection=objRegDet.getObjReg(sourceImage,connRegion);
                middleSection=grayFilter.filter(middleSection,null);
                Projection projection=new Projection();
                int jp=projection.detjumpPoint(middleSection);
                if(jp>=7&&jp<=9){
                    destImage=projection.getVerticalProjection(middleSection);
                    break;
                }
            }
        }
    }

    public void layerGrayTest() {
        /*
        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        middleSection=binaryFilter.filter(sourceImage,null);
        */

        // 彩色图像直方图均衡化
        HistogramEFilter histogramEFilter = new HistogramEFilter();
        destImage = histogramEFilter.filter(sourceImage, null);
        // 高斯模糊处理——去除图像细节,保留图像主要特征
        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter();
        destImage = gaussianBlurFilter.filter(destImage, null);
        // 分级灰度化
        ColorPicker[] colorPickers = new ColorPicker[4];
        colorPickers[0] = new BlueColorPicker(190f, 245f, 0.35f, 1f, 0.3f, 1f);
        colorPickers[1] = new YellowColorPicker(25f, 55f, 0.35f, 1f, 0.3f, 1f);
        colorPickers[2] = new WhiteColorPicker(0, 0, 0, 0.1f, 0.91f, 1f);
        colorPickers[3] = new BlackColorPicker(0, 0, 0, 0, 0, 0.35f);
        LayerGrayFilter layerGrayFilter = new LayerGrayFilter(colorPickers);
        destImage = layerGrayFilter.filter(destImage, null);
        // 分层二值化
        BufferedImage[] images = layerGrayFilter.binaryImage();
        // 灰度化
        GrayFilter grayFilter = new GrayFilter();
        middleSection = grayFilter.filter(sourceImage, null);
        int[][] se=new int[][] {{0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                {7,8,9,10,11,12,12,12,11,10,9,8,7},
                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0}};
        // 高帽变换
        HatFilter hatFilter=new HatFilter();
        middleSection=hatFilter.onTopHat(middleSection,se);
        // E算子垂直边缘检测
        EOperatorEdgeDet eOperatorEdgeDet = new EOperatorEdgeDet();
        middleSection = eOperatorEdgeDet.filter(middleSection, null);
        // 二值化
        BinaryFilter binaryFilter = new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        middleSection = binaryFilter.filter(middleSection, null);

        // 连通区域检测
        ConnRegDet connRegDet = new ConnRegDet();
        List<ConnRegion> res = connRegDet.findConnRegs(images, middleSection);
        System.out.println("连通区域数:" + res.size());



        ConnRegion connRegion = null;
        Collections.sort(res);
        Iterator<ConnRegion> ite = res.iterator();
        ObjRegDet objRegDet = new ObjRegDet();
        // 除噪结构元素
        int[][] B=new int[][]{{0,1,0},{1,1,1},{0,1,0}};
        // 闭运算
        CloseFilter smoothCloseFilter=new CloseFilter();
        smoothCloseFilter.setStructureElements(B);
        //开运算
        OpenFilter smoothOpenFilter=new OpenFilter();
        smoothOpenFilter.setStructureElements(B);
        // 图片缩放器
        ZoomFilter zoomFilter=new ZoomFilter();
        // 投影工具
        Projection projection = new Projection();
         // 真是性验证-投影特征验证
        while(ite.hasNext()) {
            connRegion = ite.next();
            middleSection = objRegDet.getObjReg(sourceImage, connRegion);
            middleSection = grayFilter.filter(middleSection, null);
            int jp = projection.detjumpPoint(middleSection);
            binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
            middleSection=binaryFilter.filter(middleSection,null);
            int width=middleSection.getWidth();
            middleSection=zoomFilter.zoom(middleSection,256);
            middleSection=smoothOpenFilter.filter(middleSection,null);
            middleSection=smoothCloseFilter.filter(middleSection,null);
            middleSection=zoomFilter.zoom(middleSection,width);
            int count=projection.countBinaryPoint(middleSection);
            int peakCount=projection.countTrough(middleSection);
            System.out.println("jp:"+jp+" count:"+count+"波峰数:"+peakCount);
            if (jp<7&&count<7||peakCount>30||peakCount<7) ite.remove();
        }

        // 第一层检查结果


        // 真实性验证-邻接链
        FacticityValidator facticityValidator=new FacticityValidator();
        // 重新获取遍历器
        ite=res.iterator();
        while (ite.hasNext()) {
            connRegion = ite.next();
            middleSection= objRegDet.getObjReg(sourceImage,connRegion);
            middleSection = grayFilter.filter(middleSection, null);
            binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
            middleSection=binaryFilter.filter(middleSection,null);
            int width=middleSection.getWidth();
            middleSection=zoomFilter.zoom(middleSection,256);
            middleSection=smoothOpenFilter.filter(middleSection,null);
            middleSection=smoothCloseFilter.filter(middleSection,null);
            middleSection=zoomFilter.zoom(middleSection,width);
            List<ConnRegion> connRegsValid = connRegDet.findConnRegs(middleSection);
            System.out.println("目标图像连通区域数量:"+connRegsValid.size());
            boolean flag=facticityValidator.validate(connRegsValid);
            if(!flag) ite.remove();
        }
        // 第二层检查结果
        System.out.println("2.候选区域数量:"+res.size());
        destImage= connRegDet.show(sourceImage,res);
        destImage=binaryFilter.filter(destImage,null);
        // 重新获取遍历器
        ite=res.iterator();
        while (ite.hasNext()) {
            connRegion = ite.next();
            middleSection= objRegDet.getObjReg(sourceImage,connRegion);
            check(middleSection);
        }

    }

    public void check(BufferedImage image){
        // 彩色图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        destImage=grayFilter.filter(image,null);

        //测试-图像转置
        TransposeFilter transposeFilter=new TransposeFilter();
        destImage=transposeFilter.filter(destImage,null);
        // 二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        destImage=binaryFilter.filter(destImage,null);
        // 测试 多结构元素边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        destImage=mseEdgeDet.filter(destImage,null);
        //倾斜校正
        // radon直线检测(图像倾斜)-水平方向检测
        RadonFilter radonFilter=new RadonFilter();
        RadonRes radonRes=radonFilter.radon(destImage,-45,45,1);
        RotationFilter rotationFilter=new RotationFilter();
        double dipAngle=radonRes.getDipAngle();
        dipAngle=45==dipAngle?0:dipAngle;
        dipAngle=-45==dipAngle?0:dipAngle;

        //垂直方向检测
        /*第一步:灰度化*/
        middleSection=grayFilter.filter(image,null);
        /*第二步:二值化*/
        middleSection=binaryFilter.filter(middleSection,null);
        /*第三步:边缘检测*/
        middleSection=mseEdgeDet.filter(middleSection,null);
        /*第四步:倾斜角度检测*/
        radonRes=radonFilter.radon(middleSection,-20,20,1);
        double verAngle=radonRes.getDipAngle();
        verAngle=20==verAngle?0:verAngle;
        verAngle=-20==verAngle?0:verAngle;
        System.out.println("水平倾斜:"+dipAngle+"垂直倾斜:"+verAngle);
        destImage=rotationFilter.shearTransform(image,-dipAngle,-verAngle);

        middleSection=grayFilter.filter(destImage,null);
        BorderDetFilter borderDetFilter=new BorderDetFilter();
        ZoomFilter zoomFilter=new ZoomFilter();
        middleSection=zoomFilter.zoom(middleSection,440);
        middleSection=borderDetFilter.dealVerBlank(middleSection,false);
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        middleSection=binaryFilter.filter(middleSection,null);
        middleSection=borderDetFilter.dealHorBorder(middleSection);
        System.out.println("\n上边界:"+borderDetFilter.getTopBorder()+"下边界:"+borderDetFilter.getBottomBorder());
        zoomFilter.setNewHeight(90);
        zoomFilter.setNewWidth(440);
        middleSection=zoomFilter.zoom(middleSection);
        Projection projection=new Projection();
        destImage=projection.getVerticalProjection(middleSection);
        // 高通滤波除噪
        middleSection=borderDetFilter.dealSPCCharacter(middleSection);
        PhotoExcision photoExcision=new PhotoExcision();
        excisions=photoExcision.doExcision(middleSection);
        excisions=NormalizationFactory.normalize(excisions);
    }

    public void checkTest(){
        // 高斯模糊处理——去除图像细节,保留图像主要特征
        GaussianBlurFilter gaussianBlurFilter=new GaussianBlurFilter();
        destImage=gaussianBlurFilter.filter(sourceImage,null);
        // 彩色图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        destImage=grayFilter.filter(destImage,null);
        // 灰度图像直方图均衡
        GrayHistogramEFilter grayHistogramEFilter=new GrayHistogramEFilter();
        destImage=grayHistogramEFilter.filter(destImage,null);

        //除噪（中值滤波）
        StatisticsFilter statisticsFilter=new StatisticsFilter();
        statisticsFilter.setType(StatisticsFilter.MEADIAN_FILTER);
        middleSection=statisticsFilter.filter(destImage,null);

        //测试-图像转置
        TransposeFilter transposeFilter=new TransposeFilter();
        destImage=transposeFilter.filter(middleSection,null);
        // 二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        destImage=binaryFilter.filter(destImage,null);
        // 测试 多结构元素边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        destImage=mseEdgeDet.filter(destImage,null);
        //hough直线检测-测试
        HoughFilter houghFilter=new HoughFilter();
        houghFilter.getCount(destImage);
        houghFilter.getHorizonRadian();
        //倾斜校正
        // radon直线检测(图像倾斜)-水平方向检测
        RadonFilter radonFilter=new RadonFilter();
        RadonRes radonRes=radonFilter.radon(destImage,-90,90,1);
        RotationFilter rotationFilter=new RotationFilter();
        double dipAngle=radonRes.getDipAngle();
        dipAngle=90==dipAngle?0:dipAngle;
        dipAngle=-90==dipAngle?0:dipAngle;
        destImage=rotationFilter.shearTransform(sourceImage,-dipAngle,0);

        //垂直方向检测
        /*第一步:灰度化*/
        middleSection=grayFilter.filter(sourceImage,null);
        /*第二步:二值化*/
        middleSection=binaryFilter.filter(middleSection,null);
        /*第三步:边缘检测*/
        middleSection=mseEdgeDet.filter(middleSection,null);
        /*第四步:倾斜角度检测*/
        radonRes=radonFilter.radon(middleSection,-45,45,1);
        double verAngle=radonRes.getDipAngle();
        verAngle=45==verAngle?0:verAngle;
        verAngle=-45==verAngle?0:verAngle;
        destImage=rotationFilter.shearTransform(destImage,0,-verAngle);

        middleSection=grayFilter.filter(destImage,null);
        BorderDetFilter borderDetFilter=new BorderDetFilter();
        middleSection=borderDetFilter.dealVerBorder(middleSection);
        System.out.println("左边界:"+borderDetFilter.getLeftBorder()+"右边界:"+borderDetFilter.getRightBorder());
        destImage=borderDetFilter.dealHorBorder(middleSection);
        System.out.println("\n上边界:"+borderDetFilter.getTopBorder()+"下边界:"+borderDetFilter.getBottomBorder());
        destImage=binaryFilter.filter(destImage,null);
        Projection projection=new Projection();
        middleSection=projection.getHorizontalProjection(destImage);
        ZoomFilter zoomFilter=new ZoomFilter();
        destImage=zoomFilter.zoom(destImage,440);

        PhotoExcision photoExcision=new PhotoExcision();
        excisions= photoExcision.doExcision(destImage);
    }



    public void writeSrcImg(){
        try{
            AbstractBufferedImageOp.writeImageFile(sourceImage);
        }catch(IOException e){}
    }

    public void writeDestImg(){
        try{
            AbstractBufferedImageOp.writeImageFile(destImage);
        }catch(IOException e){}
    }

    public void writeTemplateImg(){
        int width,height;
        if(null!=excisions){
            Iterator<ExcisionUnit> ite=excisions.iterator();
            while (ite.hasNext()){
                ExcisionUnit excisionUnit=ite.next();
                if(excisionUnit==null) continue;
                width=excisionUnit.getWidth();
                height=excisionUnit.getHeight();
                BufferedImage temp=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                AbstractBufferedImageOp.setRGB(temp,0,0,width,height,excisionUnit.getPixels());
                try{
                    AbstractBufferedImageOp.writeImageFile(temp);
                }catch(IOException e){}
            }
        }
    }

    public void setDestImage(BufferedImage destImage) {
        this.destImage = destImage;
    }


    public void openOp() {
        OpenFilter openFilter=new OpenFilter();
        openFilter.setStructureElements(new int[][]{{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1}});
        destImage=openFilter.filter(sourceImage,null);
    }


    public void closeOp() {
        CloseFilter openFilter=new CloseFilter();
        openFilter.setStructureElements(new int[][]{{1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1}});
        destImage=openFilter.filter(sourceImage,null);
    }
}
