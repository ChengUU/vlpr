package com.sgc.segmentation;

import com.sgc.jama.Matrix;
import com.sgc.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Float.NaN;

/**
 * Created by ChengXX on 2017/3/13.
 */
public class KFC_Means {
    double m;
    double r;
    int K;
    int N;
    int D;
    double maxNum;
    double[][] U=null;
    double[][] _U=null;
    int[] I=null;
    private boolean initUik=false;
    List<List<Double>> points=null;
    List<List<Double>> clusters=null;
    List<Double> obj_fcn=new ArrayList<>();
    public KFC_Means(List<List<Double>> points, List<List<Double>> clusters){
        this(points,clusters,2);
    }
    public KFC_Means(List<List<Double>> points, List<List<Double>> clusters, double m){
        this(points,clusters,m,150);
    }
    public KFC_Means(List<List<Double>> points, List<List<Double>> clusters, double m, double r){
        this(points,clusters,m,r,100);
    }
    public KFC_Means(List<List<Double>> points, List<List<Double>> clusters, double m, double r, double maxNum){
        this.points=points;
        this.clusters=clusters;
        this.m=m;
        this.r=r;
        this.D=points.get(0).size();
        this.maxNum=maxNum;
        this.K=clusters.size();
        this.N=points.size();
        U=new double[K][N];
        _U=new double[K][N];
        I=new int[N];
    }
    public void initKFCMUik(){
        double[] sum=new double[N];
        Arrays.fill(sum,0);
        Random random=new Random();
        // 产生随机数填充隶属度矩阵
        for(int i=0;i<N;i++){
            for(int j=0;j<K;j++){
                int value=random.nextInt(N)+1;
                U[j][i]=value;
                sum[i]+=value;
            }
        }
        // 归一化隶属度矩阵
        for(int i=0;i<N;i++){
            for(int j=0;j<K;j++){
                U[j][i]/=sum[i];
//                System.out.print("  U[j][i]="+U[j][i]);
            }
//            System.out.println();
        }
        initUik=true;
    }
    protected   void calculateUik(){
        List<Double> temp=null;
        for(int i=0;i<K;i++){
            for(int j=0;j<N;j++) _U[i][j]=U[i][j];
        }
        for(int i=0;i<N;i++){
            temp=points.get(i);
            for(int j=0;j<K;j++){
                //隶属度公式分子部分->当前点对聚类中心j的贡献值
                double uCurrent=Math.pow(1-gaussian_rbfkernel(temp,clusters.get(j)),-1.0/(this.m-1));
                double uAll=0;
                //隶属度分母部分->当前点对所有聚类中心得贡献值
                for(int k=0;k<K;k++){
                    uAll+=Math.pow(1-gaussian_rbfkernel(temp,clusters.get(k)),-1.0/(this.m-1));
                }
                //隶属度计算
                U[j][i]=uCurrent/uAll;
            }
        }
    }
    protected void updateCluster(){
        double um=0;
        double _um=0;
        List<Double> temp=new ArrayList<>();
        List<Double> point;
        for(int i=0;i<D;i++) temp.add(0.0);
        for(int i=0;i<K;i++){
            _um=0;
            for(int j=0;j<D;j++) temp.set(j,0.0);
            for(int j=0;j<N;j++){
                point=points.get(j);
                um=Math.pow(U[i][j],this.m)*gaussian_rbfkernel(points.get(j),clusters.get(i));
                _um+=um;
                //求各个分量的累加值
                for(int k=0;k<D;k++){
                    double dk=temp.get(k);
                    dk+=um*point.get(k);
                    temp.set(k,dk);
                }
            }
            //更新类心i
            point=clusters.get(i);
            for(int j=0;j<D;j++){
                double dk=temp.get(j);
                dk/=_um;
                point.set(j,dk);
            }
        }
    }

    public int[] cluster(){
        if(null==points||0==points.size()) return I;
        if(!initUik) calculateUik();
        for(int i=0;i<maxNum;i++){
//            System.out.println("迭代次数"+i);
            updateCluster();
            calculateUik();
            obj_fcn();
//            System.out.print("隶属度相似度:"+obj_fcn.get(i));
            if(E>obj_fcn.get(i)) break;
        }
        double max=Double.MIN_VALUE;
        int k=0;
        for(int i=0;i<N;i++){
            max=Double.MIN_VALUE;
            k=-1;
            for(int j=0;j<K;j++){
//                System.out.println("U[j][i]"+U[j][i]);
                if(max<U[j][i]){
                    max=U[j][i];
                    k=j;
                }
            }
            I[i]=k;
        }
        return I;
    }
    public List<List<Double>> getClusters() {
        return this.clusters;
    }

    //高斯核函数
    protected double  gaussian_kernel(double distance){
        return Math.exp(-1.0*(distance*distance)/(this.r*this.r));
    }

    protected double gaussian_rbfkernel(List<Double> v1,List<Double> v2){
        double distance=Util.euclidean_distance(v1,v2);
        return Math.exp(-1.0*(distance*distance)/(this.r*this.r));
    }
    protected void obj_fcn(){

        double obj_value=0;
        Matrix U2=new Matrix(U);
        Matrix U1=new Matrix(_U);
        Matrix res=U2.minus(U1);
        obj_value=res.norm1();
        obj_fcn.add(obj_value);
    }

    public static final double E=0.0000001;

}
