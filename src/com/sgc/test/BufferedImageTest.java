package com.sgc.test;

import com.sgc.photo.TEST;
import com.sgc.segmentation.Cluster;
import com.sgc.segmentation.FC_Means;
import com.sgc.segmentation.KFC_Means;
import com.sgc.segmentation.MeanShift;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/9.
 */
public class BufferedImageTest {
    //0.288
    public static double t=0.265625;
    private static String src="D:\\ChengXX\\Cluster\\Lena128128.jpg";
    private static String PHOTO_OUTPUT_FILE="D:\\ChengXX\\Cluster\\photo_detail2.txt";
    private static String PHOTO_SEGMENTATION="D:\\ChengXX\\Cluster\\photo_segmentation.txt";
    public static void main(String[] args)throws Exception{
        TEST test=null;
        try{
            File f=new File(src);
            test=new TEST(ImageIO.read(f));
        }catch(Exception e){
            e.printStackTrace();
        }
        long start=System.currentTimeMillis();
        List<List<Double>> samples=test.getSamples();
        long end=System.currentTimeMillis();
        System.out.println("Get Samples time:"+(end-start)/1000.0);
        System.out.println("Samples size:"+samples.size());
        int size=samples.size();
        File outfile=new File(PHOTO_OUTPUT_FILE);
        BufferedWriter writer=new BufferedWriter(new FileWriter(outfile));
        for(int i=0;i<size;i++){
            List<Double> sample=samples.get(i);
            for(int j=0;j<8;j++){
                writer.write(String.format("    %.2f",sample.get(j)));
            }
            writer.write("\r\n");
        }
        writer.close();

        start=System.currentTimeMillis();
        double bandwidth=MeanShift.getBandwidth(samples,t);
        end=System.currentTimeMillis();
        System.out.println("Get Bandwidth time:"+(end-start)/1000.0);
        System.out.println("t:"+t+"bandwidth:"+bandwidth);

        MeanShift msh=new MeanShift();

        start=System.currentTimeMillis();
        List<Cluster> tempClusters=msh.cluster(samples);


        end=System.currentTimeMillis();
        System.out.println("MeanShift time:"+(end-start)/1000.0);
        size=tempClusters.size();
        List<List<Double>> clusters=new ArrayList<>();
        for(int i=0;i<size;i++){
            clusters.add(tempClusters.get(i).mode);
        }
        System.out.println("Clusters size:"+clusters.size());

        FC_Means fcm=new FC_Means(samples,clusters,2);
        start=System.currentTimeMillis();
        fcm.kfcm();
        int[] U=fcm.getI();
        end=System.currentTimeMillis();
        System.out.println("KFCM time:"+(end-start)/1000.0);
        outfile=new File(PHOTO_SEGMENTATION);
        writer=new BufferedWriter(new FileWriter(outfile));
        for(int i=0;i<U.length;i++){
                writer.write(String.format("    %-4d",U[i]));
        }
        writer.close();
//        List<Double> rgb=new ArrayList<Double>();
//        rgb.add(172.0);
//        rgb.add(187.0);
//        rgb.add(43.0);
//        test.rgbToLab(rgb);
//        for(int i=0;i<rgb.size();i++) {
//            System.out.print(" "+rgb.get(i));
//        }
//        rgb.set(0,49.0);
//        rgb.set(1,141.0);
//        rgb.set(2,49.0);
//        test.rgbToLab(rgb);
//        for(int i=0;i<rgb.size();i++) {
//            System.out.print(" "+rgb.get(i));
//        }
    }
}
