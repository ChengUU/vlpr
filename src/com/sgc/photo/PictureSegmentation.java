package com.sgc.photo;

import com.sgc.segmentation.Cluster;
import com.sgc.segmentation.FC_Means;
import com.sgc.segmentation.KFC_Means;
import com.sgc.segmentation.MeanShift;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/11.
 */
public class PictureSegmentation extends AbstractBufferedImageOp {
    private int[] fogLookUp=new int[256];
    private double[] bandwidths;
    private int[] segs;
//    public PictureSegmentation(){this(0.095);}
    public PictureSegmentation(double[] banadwidths,int[] segs){
        this.bandwidths=banadwidths;
        this.segs=segs;
        buildFogLookUpTable();
    }

    public BufferedImage filter(BufferedImage srcImage,BufferedImage dest){
        dest=new BufferedImage(srcImage.getWidth(),srcImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        TEST test=null;
        try{
            test=new TEST(srcImage);
        }catch(Exception e){System.out.println("初始化TEST对象失败");}
        //获取样本数据
        long start=System.currentTimeMillis();
        List<List<Double>> samples=test.getSamples();
        int size=samples.size();
        long end=System.currentTimeMillis();
        System.out.println("Get Samples time:"+(end-start)/1000.0);
        System.out.println("Samples size:"+samples.size());
        //获取Meanshift带宽
        List<List<Double>> labs=new ArrayList<>();
        List<List<Double>> textures=new ArrayList<>();
        List<List<Double>> locations=new ArrayList<>();
        for(int i=0;i<size;i++){
            labs.add(samples.get(i).subList(0,3));
            textures.add(samples.get(i).subList(3,6));
            locations.add(samples.get(i).subList(6,8));
        }

        //使用Meanshift算法进行初始聚类


        //获取样本带宽
        double bandwidth=MeanShift.getBandwidth(samples,0.5);
        MeanShift msh=new MeanShift(bandwidth);
        start=System.currentTimeMillis();
        List<Cluster> tempClusters=msh.mean_shift_cluster(samples,bandwidth);
        end=System.currentTimeMillis();
        System.out.println("MeanShift time:"+(end-start)/1000.0);
        size=tempClusters.size();
        List<List<Double>> clusters=new ArrayList<>();
        for(int i=0;i<size;i++){
            clusters.add(tempClusters.get(i).mode);
        }
        System.out.println("Clusters size:"+clusters.size());
        //利用初始聚类结果进行快速分割
//        KFC_Means kfcm=new KFC_Means(samples,clusters,2,150);
        KFC_Means fcm=new KFC_Means(samples,clusters);
        int[] I=fcm.cluster();
        start=System.currentTimeMillis();
        end=System.currentTimeMillis();
        System.out.println("KFCM time:"+(end-start)/1000.0);
        List<List<Double>> rgbs=test.getRGBs();
        int ck=0;
        int M=test.getM();
        int N=test.getN();
        int M2=M/TEST.BLOCK;
        int N2=N/TEST.BLOCK;
        int xi=0;
        int yi=0;
        for(int i=0;i<I.length;i++){
            ck=I[i]%STYLE_NUM;
            xi=i/M2*TEST.BLOCK;
            yi=i%M2*TEST.BLOCK;
            int[] pixle=getStylePixles(styles[ck],TEST.BLOCK,TEST.BLOCK);
            setRGB(dest,xi,yi,TEST.BLOCK,TEST.BLOCK,pixle);
        }
        return dest;

    }

    //空气风格
    public int[]  getAirStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        pixle[0]=clamp(tg+tb)/2;
        pixle[1]=clamp(tr+tb)/2;
        pixle[2]=clamp(tg+tr)/2;
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //燃情风格
    public int[]  getPassionStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        int gray=(tr+tg+tb)/3;
        pixle[0]=clamp(gray*3);
        pixle[1]=gray;
        pixle[2]=gray/3;
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //雾风格
    public int[]  getFogStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        pixle[0]=fogLookUp[tr];
        pixle[1]=fogLookUp[tg];
        pixle[2]=fogLookUp[tb];
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //熔岩风格
    public int[]  getLavaStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        int gray=(tr+tg+tb)/3;
        pixle[0]=gray;
        pixle[1]=Math.abs(tb-128);
        pixle[2]=Math.abs(tb-128);
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //金属风格
    public int[]  getMetaStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        float r=Math.abs(tr-64);
        float g=Math.abs(r-64);
        float b=Math.abs(g-64);
        float gray=((222*r+707*g+71*b)/1000);
        r=gray+70;
        r=r+(((r-128)*100)/100f);
        g=gray+65;
        g=g+(((g-128)*100)/100f);
        b=gray+75;
        b=b+(((b-128)*100)/100f);
        pixle[0]=clamp((int)r);
        pixle[1]=clamp((int)g);
        pixle[2]=clamp((int)b);
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //海洋风格
    public int[]  getSeaStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        int gray=(tr+tg+tb)/3;
        pixle[0]=clamp(gray/3);
        pixle[1]=gray;
        pixle[2]=clamp(gray*3);
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    //湖水风格
    public int[]  getLakeStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        int gray=(tr+tg+tb)/3;
        pixle[0]=clamp(gray-tg-tb);
        pixle[1]=clamp(gray-pixle[0]-tb);
        pixle[2]=clamp(gray-pixle[0]-pixle[1]);
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    public int[]  getFrozenStylePixles(int tr,int tg,int tb,int width,int height){
        int[] pixle=new int[3];
        int gray=(tr+tg+tb)/3;
        pixle[0]=clamp((int)Math.abs((tr-tg-tb)*1.5));
        pixle[1]=clamp((int)Math.abs((tr-tg-pixle[0])*1.5));
        pixle[2]=clamp((int)Math.abs((tr-pixle[1]-pixle[0])*1.5));
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }

    //鸨色
    public int[]  getStylePixles(int[] rgbs,int width,int height){
        int[] pixle=new int[3];
        pixle[0]=clamp(rgbs[0]);
        pixle[1]=clamp(rgbs[1]);
        pixle[2]=clamp(rgbs[2]);
        int rgb=pixle[0]<<16|pixle[1]<<8|pixle[2];
        int length=width*height;
        int[] pixles=new int[length];
        int index=0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                index=i*width+j;
                pixles[index]=rgb;
            }
        }
        return pixles;
    }
    private void buildFogLookUpTable(){
        int fogLimit=40;
        int length=fogLookUp.length;
        for(int i=0;i<length;i++){
            if(i>127){
                fogLookUp[i]=i-fogLimit;
                if(fogLookUp[i]<127) fogLookUp[i]=127;
            }else{
                fogLookUp[i]=i+fogLimit;
                if(fogLookUp[i]>127) fogLookUp[i]=127;
            }
        }
    }
    public final static int[][] styles={{0xf7,0xac,0xbc},{0xde,0xab,0x8a},{0x81,0x79,0x36},
                                {0x44,0x46,0x93},{0xef,0x5b,0x9c},{0xfe,0xdc,0xbd},
                                {0x7f,0x75,0x22},{0x2b,0x44,0x90},{0xfe,0xee,0xed},
                                {0xf4,0x79,0x20},{0x80,0x75,0x2c},{0x2a,0x5c,0xaa},
                                {0xf0,0x5b,0x72},{0x90,0x5a,0x3d},{0x87,0x84,0x3b},
                                {0x22,0x4b,0x8f},{0xf1,0x5b,0x6c},{0x8f,0x4b,0x2e},
                                {0x72,0x69,0x30},{0x00,0x3a,0x6c},{0xf8,0xab,0xa6},
                                {0x87,0x48,0x1f},{0x45,0x49,0x26},{0x10,0x2b,0x6a},
                                {0xf6,0x9c,0x9f},{0x5f,0x3c,0x23},{0x2e,0x3a,0x1f},
                                {0x42,0x6a,0xb3},{0xf5,0x8f,0x98},{0x6b,0x47,0x3c},
                                {0x4d,0x4f,0x36},{0x46,0x48,0x5f},{0xca,0x86,0x87},
                                {0xfa,0xa7,0x55},{0xb7,0xba,0x6b},{0x4e,0x72,0xb8}};
    public static final int STYLE_NUM=36;
}
