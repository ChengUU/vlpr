package com.sgc.segmentation;

import com.sgc.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/8.
 */
public class FC_Means {
    private List<Double> obj_fcn=new ArrayList<Double>();
    private double m=2;
    private int maxNum=100;
    private double EPSILON=0.0000001;
    private List<List<Double>> points;
    private List<List<Double>> clusters;
    private double[][] out;
    private double[][] U;
    private int[] I;
    private int K;
    private int N;

    public FC_Means(List<List<Double>> points, List<List<Double>> clusters){
        this(points,clusters,2);
    }
    public FC_Means(List<List<Double>> points, List<List<Double>> clusters, double m){
        this.clusters=clusters;
        this.points=points;
        this.K=clusters.size();
        this.N=points.size();
        out=new double[K][N];
        this.U=new double[K][N];
        this.m=m;
        this.I=new int[N];
    }

    private  void calculateUik(){
        double uCurrent=0;
        double uAll=0;
        for(int i=0;i<K;i++){
            uCurrent=0;
            for(int j=0;j<N;j++){
                //uCurrent=||Xj-Ci||
                out[i][j]=Util.euclidean_distance(clusters.get(i),points.get(j));
                uCurrent=Math.pow(out[i][j],2.0/(m-1));
                uAll=0;
                //E(uCurrent/||Xj-Ck||)
                for(int n=0;n<K;n++){
                    double distance=Util.euclidean_distance(clusters.get(n),points.get(j));
                    uAll+=(uCurrent/Math.pow(distance,2.0/(m-1)));
                }
                U[i][j]=1.0/uAll;
            }
        }
        obj_fcn();
    }
    private void updateCluster(){
        //样本j对聚类中心i的隶属度
        double uij=0;
        double um=0;
        //样本特征维度
        int d=clusters.get(0).size();
        List<Double> temp=new ArrayList<Double>(d);
        for(int i=0;i<d;i++){
            temp.add(0.0);
        }
        for(int i=0;i<K;i++){
            uij=0;
            for(int index=0;index<d;index++){
                temp.set(index,0.0);
            }
            for(int n=0;n<N;n++){

                um=Math.pow(U[i][n],m);
                uij+=um;
                List<Double> xj=points.get(n);
                //对样本points[n]各特征分量进行乘运算
                for(int h=0;h<d;h++){
                    double xjd=temp.get(h);
                    xjd+=(um*xj.get(h));
                    temp.set(h,xjd);
                }
            }
            List<Double> clus=clusters.get(i);
            for(int index=0;index<d;index++){
                double xjd=temp.get(index);
                xjd/=uij;
                clus.set(index,xjd);
            }
        }
    }
    private void obj_fcn(){

        double obj_value=0;
        for(int i=0;i<K;i++){
            for(int j=0;j<N;j++){
                obj_value+=(out[i][j]*out[i][j]*U[i][j]);
            }
        }
        obj_fcn.add(obj_value);
    }


    public double[][] kfcm(){
        calculateUik();
        for(int i=0;i<maxNum;i++) {
            updateCluster();
            calculateUik();
            if(i>1&&Math.abs(obj_fcn.get(i)-obj_fcn.get(i-1))<=EPSILON) break;
        }
        double max=Double.MIN_VALUE;
        int ck=0;
        for(int i=0;i<N;i++){
            max=Double.MIN_VALUE;
            for(int j=0;j<K;j++){
                if(max<U[j][i]){
                    max=U[j][i];
                    ck=j;
                }
            }
            I[i]=ck;
        }
        return U;
    }

    public  int[] getI(){
        return I;
    }

    public List<List<Double>> getClusters(){
        return this.clusters;
    }
}
