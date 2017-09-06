package com.sgc.test;

import com.sgc.segmentation.Cluster;
import com.sgc.segmentation.KFC_Means;
import com.sgc.segmentation.MeanShift;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/13.
 */
public class KFC_MeansTest {
    public static String INPUT_FILE="D:/ChengXX/Cluster/input.txt";
    public static String MEANSHIFT_OUTPUT_FILE="D:/ChengXX/Cluster/msh_out.txt";
    public static String KFCM_OUTPUT_FILE="D:/ChengXX/Cluster/kfcm_out.txt";
    public static double t=0.25;

    public static void main(String[] args){
        File file=new File(INPUT_FILE);
        BufferedReader reader=null;
        List<List<Double>> points=new ArrayList<List<Double>>();
        try{
            reader=new BufferedReader(new FileReader(file));
            String tempString=null;
            int line=-1;
            while(null!=(tempString=reader.readLine())){
                if(tempString.isEmpty()) break;
                String[] original_data=tempString.split("  ");
                double x=Double.parseDouble(original_data[0]);
                double y=Double.parseDouble(original_data[1]);
                List<Double> point=new ArrayList<Double>();
                point.add(x);
                point.add(y);
                points.add(point);
            }
            reader.close();
        }catch (IOException e){
            System.out.println("数据读取出错......");
        }
        System.out.println("points_size:"+points.size());
        MeanShift msh=new MeanShift();
        double bandwidth=MeanShift.getBandwidth(points,t);
        System.out.println("bandwidth:"+bandwidth);
        long start=System.currentTimeMillis();
        List<Cluster> clusters=msh.cluster(points);
        long end=System.currentTimeMillis();
        System.out.println("time:"+(end-start)/1000.0);
        System.out.println("Clusters size:"+clusters.size());
        File result=new File(MEANSHIFT_OUTPUT_FILE);
        BufferedWriter writer=null;
        List<List<Double>> kfcmClusters=new ArrayList<List<Double>>();
        try{
            writer=new BufferedWriter(new FileWriter(result));
            int size=clusters.size();
            for(int i=0;i<size;i++){
                Cluster clus=clusters.get(i);
                kfcmClusters.add(clus.mode);
                writer.write("clus "+i+":");
                writer.write("\r\nmode="+clus.mode);
                writer.write("\r\n");
            }
            writer.close();
        }catch(IOException e){
            System.out.println("数据打印出错......");
        }
        KFC_Means kfcm=new KFC_Means(points,kfcmClusters,2.0,150);
        start=System.currentTimeMillis();
        int[] I= kfcm.cluster();
        end=System.currentTimeMillis();
        System.out.println("time:"+(end-start)/1000.0);
        kfcmClusters=kfcm.getClusters();
        File kfcmFile=new File(KFCM_OUTPUT_FILE);
        try{
            writer=new BufferedWriter(new FileWriter(kfcmFile));
            int size=kfcmClusters.size();
            for(int i=0;i<size;i++){
                List<Double> clus=kfcmClusters.get(i);
                writer.write("clus "+i+":");
                writer.write("\r\nmode="+clus);
                writer.write("\r\n");
            }
            for(int j=0;j<points.size();j++){
                writer.write(String.format("%d->%d   ", j,I[j]));
            }

            writer.close();
        }catch(IOException e){
            System.out.println("数据打印出错......");
        }
    }
}
