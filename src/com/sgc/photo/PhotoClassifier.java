package com.sgc.photo;

import com.sgc.jama.Matrix;
import com.sgc.res.GrayClusSample;
import com.sgc.res.RGColorSpace;
import com.sgc.res.RGColorSpcSample;
import com.sgc.res.RGHis;
import com.sgc.segmentation.K_FCMeans;
import com.sgc.service.RGBToRG;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by ChengXX on 2017/4/26.
 */
public class PhotoClassifier {
    public static PhotoClassifier instance;
    private PhotoClassifier(){}
    public static PhotoClassifier getInstance(){
        if(null==instance){
            synchronized (PhotoClassifier.class){
                if(null==instance){
                    instance=new PhotoClassifier();
                }
            }
        }
        return instance;
    }

    public int classify(BufferedImage image){
        int cls=DAYTIME_NORMAL;
        double jd=computeRGColorSpcClusJd(image);
        // 二值化阀值与灰度均值之比(明暗程度)
        // 灰度均值
        GrayFilter grayFilter=new GrayFilter();
        BufferedImage temp=grayFilter.filter(image,null);
        double mean=grayFilter.getMean();
        // 最大类间法二值化阀值
        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        binaryFilter.filter(temp,null);
        int binThreshold=binaryFilter.getThreshold();
        // Tx
        double Tx=binThreshold/mean;
        double[] rlm=computeLightenessLM(image,3);
//        System.out.println("\njd="+jd);
//        System.out.println("T*="+Tx);
//        System.out.println("mean="+mean);
//        System.out.println("rlm[0]="+rlm[0]);
//        System.out.println("rlm[3]="+rlm[3]);
        if(jd<=50&&mean>=28){
            if(mean>170) cls= DAYTIME_BLAZE;
            else if(mean>102&&mean<=170) cls=DAYTIME_NORMAL;
            else cls=NIGHT_NORMAL;
        }else{
            if (jd>50&&jd<=120) cls=DAYTIME_LOW_LIGHT;
            else if(mean<15) cls=NIGHT_LOW_LIGHT;
            else if(jd<=160&&Tx<=2&&rlm[0]>=30) cls=DAYTIME_LOW_LIGHT;
            else if(mean>120||(rlm[3]>=3&&rlm[0]>=30)||jd>2000) cls=NIGHT_BLAZE;
            else cls=NIGHT_LOW_LIGHT;
        }
        return cls;
    }

    public double computeRGColorSpcClusJd(BufferedImage image){
        double jd=0;
        RGColorSpace rgColorSpace= RGBToRG.rgbToRG(image);
        RGHis rgHis=RGBToRG.getRGHis(rgColorSpace,256,256);
        RGColorSpcSample rgColorSpcSample =RGBToRG.getSamples(rgHis);
        List<List<Double>> samples=rgColorSpcSample.samples;
        List<Double> weight=rgColorSpcSample.p;
        // 打印样本
        int size=samples.size();
//        System.out.println(size);
        List<List<Double>> centers=new ArrayList<>();
        Random random=new Random();
        int count=0;
        for(;count<3;){
            int index=random.nextInt(size);
            List<Double> temp=samples.get(index);
            if(!centers.contains(temp)) {
                List<Double> cluster=new ArrayList<>();
                double x=temp.get(0)+0.5;
                double y=temp.get(1)+0.5;
                cluster.add(x);
                cluster.add(y);
                centers.add(cluster);
                count++;
            }
        }
        // 打印随机聚类中心
//        System.out.println(centers);
        K_FCMeans k_fcMeans=new K_FCMeans(samples,centers,weight);
        k_fcMeans.initKFCMUik();
        int[] I=k_fcMeans.cluster();
        // 打印聚类结果中心
//        System.out.println();
//        System.out.println(centers);
        // 计算类内类间离散度矩阵
        Matrix matrix1=k_fcMeans.sb(rgHis);
        Matrix matrix2=k_fcMeans.sw(rgHis);
        Matrix matrix=matrix1.plus(matrix2);
        jd=matrix.trace();
//        System.out.println("类间离散度与类内离散度="+jd);
        return jd;
    }

    public double[] computeLightenessLM(BufferedImage image,int K){
        double rlm=1;
        // 对灰度图像进行聚类
        //获取聚类样本
        HistogramFIlter histogramFilter=new HistogramFIlter();
        GrayClusSample grayClusSample=histogramFilter.getSamples(image);
        // 灰度样本数据
        List<List<Double>>samples=grayClusSample.samples;
        // 灰度权重
        List<Double> weight=grayClusSample.weight;
        // 初始化聚类中心
        List<List<Double>>centers=new ArrayList<>();
        int size=samples.size();
        int count=0;
        // 随机数生成器
        Random random=new Random();
        for(;count<K;){
            int index=random.nextInt(size);
            List<Double> center=samples.get(index);
            if(!centers.contains(center)) {
                List<Double> cluster=new ArrayList<>();
                double x=center.get(0)+0.5;
                cluster.add(x);
                centers.add(cluster);
                count++;
            }
        }

        K_FCMeans k_fcMeans=new K_FCMeans(samples,centers,weight);
        k_fcMeans.initKFCMUik();
        int[] I=k_fcMeans.cluster();
        // 查找低亮度去和中亮度区
        Map<Double,Double> lmh=new HashMap<>();
        for(int i=0;i<K;i++){
            double max=Double.MIN_VALUE;
            double pixelsCount=0;
            for(int j=0;j<size;j++){
                int k=I[j];
                if(k!=i) continue;
                pixelsCount+=weight.get(j);
                double gray=samples.get(j).get(0);
                if(max<gray) max=gray;
            }
            lmh.put(max,pixelsCount);
//            System.out.println(String.format("\n第%d类灰度极值=%.2f,像素总数=%.2f",i,max,pixelsCount));
        }
        Double[] lmhVal=lmh.keySet().toArray(new Double[K]);
        Arrays.sort(lmhVal);
        double[] res=new double[K+1];
        for(int i=0;i<lmhVal.length;i++){
            res[i]=lmhVal[i];
        }
        double lowCount=lmh.get(res[0]);
        double middleCount=lmh.get(res[K-2]);

//        System.out.println("低亮度区-low="+res[0]+"像素总数="+lowCount);
//        System.out.println("中亮度区-middle="+res[K-2]+"像素总数="+middleCount);
        res[K]=lowCount/middleCount;
//        System.out.println("低亮度区同中亮度区像素总数之比="+res[K]);
        return res;
    }


    public final static int DAYTIME_BLAZE=0;
    public final static int DAYTIME_NORMAL=1;
    public final static int DAYTIME_LOW_LIGHT=2;
    public final static int NIGHT_BLAZE=3;
    public final static int NIGHT_NORMAL=4;
    public final static int NIGHT_LOW_LIGHT=5;

    public static String[] PHOTO_TYPE=new String[]{"白天强光型","白天正常型","白天弱光型","夜晚强光型","夜晚正常型","夜晚弱光型"};
}
