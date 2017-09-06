package com.sgc.segmentation;


import com.sgc.jama.Matrix;
import com.sgc.res.RGHis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by ChengXX on 2017/4/24.
 */
public class K_FCMeans extends KFC_Means {
    // 样本权重
    private List<Double> weight;
    public K_FCMeans(List<List<Double>> points, List<List<Double>> clusters,List<Double> weight){
        this(points,clusters,2,weight);
    }
    public K_FCMeans(List<List<Double>> points, List<List<Double>> clusters, double m,List<Double> weight){
        this(points,clusters,m,100,weight);
    }
    public K_FCMeans(List<List<Double>> points, List<List<Double>> clusters, double m, double r,List<Double> weight){
        this(points,clusters,m,r,60,weight);
    }
    public K_FCMeans(List<List<Double>> points, List<List<Double>> clusters, double m, double r, double maxNum,List<Double> weight){
        super(points,clusters,m,r,maxNum);
        this.weight=weight;
    }
    public  void calculateUik(){
       super.calculateUik();
    }
    public void updateCluster(){
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
                um=weight.get(j)*Math.pow(U[i][j],this.m)*gaussian_rbfkernel(point,clusters.get(i));
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
    // 类间离散度矩阵
    public Matrix sb(RGHis rgHis){
        // 第k类样直方图统计值占总的样本直方图概率
        double[] p=new double[K];
        Arrays.fill(p,0.0);
        // 第k类直方图统计值
        int[] hk=new int[K];
        Arrays.fill(hk,0);
        // 样本直方图统计值
        int hn=0;
        int len=I.length;
        for(int i=0;i<len;i++){
            int k=I[i];
            double x=points.get(i).get(0);
            double y=points.get(i).get(1);
            int col=(int)x;
            int row=(int)y;
            hk[k]+=rgHis.his[row][col];
            hn+=rgHis.his[row][col];
        }
        for(int i=0;i<K;i++) p[i]=1.0*hk[i]/hn;
        Matrix matrix=new Matrix(D,D);
        double[] res=new double[D];
        Matrix matrix1 = new Matrix(D, 1);
        for(int i=0;i<K-1;i++) {
            Double[] vk = (Double[]) clusters.get(i).toArray(new Double[D]);
            for (int j = i + 1; j < K; j++) {
                Double[] vl = (Double[]) clusters.get(j).toArray(new Double[D]);
                // 计算聚类中心差
                for(int row=0;row<D;row++) res[row]=vk[row]-vl[row];
                // 构造矩阵
                for (int row = 0; row < D; row++)
                    matrix1.set(row, 0, res[row]);
                Matrix matrix2=matrix1.copy();
                matrix2=matrix2.transpose();
                Matrix matrix3=matrix1.times(matrix2);
                matrix3=matrix3.times(p[j]);
                matrix=matrix.plus(matrix3);
            }
            matrix=matrix.times(p[i]);
        }
        return matrix;
    }

    // 类内离散度矩阵
    public Matrix sw(RGHis rgHis) {
        // 计算各类样本在色度空间直方图中出现总概率
        double[] p = new double[K];
        Arrays.fill(p, 0.0);
        // 第k类样本数
        int[] nk = new int[K];
        Arrays.fill(nk, 0);
        // 第k类直方图统计值
        int[] hk = new int[K];
        Arrays.fill(hk, 0);
        // 样本直方图统计值
        int hn = 0;
        int len = I.length;
        // 样本均值
        List<List<Double>> mi = new ArrayList<>();
        for (int i = 0; i < K; i++) mi.add(null);
        for (int i = 0; i < len; i++) {
            int k = I[i];
            double x = points.get(i).get(0);
            double y = points.get(i).get(1);
            int col = (int) x;
            int row = (int) y;
            hk[k] += rgHis.his[row][col];
            hn += rgHis.his[row][col];
            nk[k] += 1;
        }
        for(int k=0;k<K;k++){
            List<Double> temp = new ArrayList<>();
            for (int d = 0; d < D; d++) temp.add(0.0);
            for(int j=0;j<len;j++){
                int i=I[j];
                if(k!=i) continue;
                for (int d = 0; d < D; d++) {
                    double count = temp.get(d);
                    count += points.get(j).get(d);
                    temp.set(d, count);
                }
            }
            mi.set(k,temp);
        }
        for (int i = 0; i < K; i++) {
            p[i] = 1.0 * hk[i] / hn;
            for (int d = 0; d < D; d++) {
                double count = mi.get(i).get(d);
                count /= nk[i];
                mi.get(i).set(d, count);
            }
        }
//        System.out.println("mi="+mi);
//        System.out.println("clusters="+clusters);
        // 临时计算中间变量
        Matrix matrix = new Matrix(D, D);
        // 测试类内离散度
        Matrix matrix1 = new Matrix(D, 1);
        for (int i = 0; i < K; i++) {
            Matrix temp = new Matrix(D, D);
            Double[] vk = (Double[]) mi.get(i).toArray(new Double[D]);
            for (int j = 0; j < N; j++) {
                int k = I[j];
                if (i != k) continue;
                Double[] xl = (Double[]) points.get(j).toArray(new Double[D]);
                // 构造矩阵
                for (int row = 0; row < D; row++) {
                    matrix1.set(row, 0, xl[row] - vk[row]);
                }
                Matrix matrix2 = matrix1.copy();
                matrix2 = matrix2.transpose();
                Matrix matrix3 = matrix1.times(matrix2);
                temp = temp.plus(matrix3);
            }
            double value = p[i]/nk[i]  ;
            temp = temp.times(value);
            matrix = matrix.plus(temp);
        }
//        System.out.println("sw="+matrix.trace());
        return matrix;
    }

    public List<Double> getWeight() {
        return weight;
    }

    public void setWeight(List<Double> weight) {
        this.weight = weight;
    }
}
