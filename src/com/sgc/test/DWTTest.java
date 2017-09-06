package com.sgc.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/10.
 */
public class DWTTest {

    private  int max(int a,int b){
        return  a>b?a:b;
    }
    private  int min(int a,int b){
        return a<b?a:b;
    }

    private  double[] conv(double[] A,double[] B){
        int l1=A.length;
        int l2=B.length;
        int l3=l1+l2-1;
        double[] res=new double[l3];
        for(int i=0;i<l3;i++){
            res[i]=0;
        }
        for(int i=0;i<l3;i++){
            for(int j=max(0,i+1-l2);j<=min(i,l1-1);j++){
                res[i]+=A[j]*B[i-j];
            }
        }
        return res;
    }

    private  double[] downspl(double[] X){
        int N=X.length;
        int M=N/2;
        double[] res=new double[M];
        for(int i=0;i<M;i++){
            res[i]=X[i*2+1];
        }
        return res;
    }

    /**
     * 对输入序列X进行一维离散小波分解，输出分解序列[]
     * @param X 输入序列
     * @param lpd 低通滤波器
     * @param hpd 高通滤波器
     * @param dim 分解级数
     * @return
     */

    public  List<List<Double>> mydwt(double[] X,double[] lpd,double[] hpd,int dim){
        List<List<Double>> res=new ArrayList<>();
        List<Double> cd=new ArrayList<>();
        List<Double> ca=new ArrayList<>();
        double[] cA=X;
        for(int i=0;i<dim;i++){
            //低通滤波
            double[] cv1=conv(cA,lpd);
            //高通滤波
            double[] cvh=conv(cA,hpd);
            cvh=downspl(cvh);
            cA=downspl(cv1);
            for(int j=0;j<cvh.length;j++){
                cd.add(cvh[j]);
            }
        }
        for(int i=0;i<cA.length;i++){
            ca.add(cA[i]);
        }
        res.add(ca);
        res.add(cd);
        return res;
    }

    /**
     *
     * @param _X 输入矩阵，为r*c维矩阵
     * @return 4个大小相等的子矩阵，大小均为r/2*c/2 LL:低频部分分解系数 HL:垂直方向分解系数  LH：水平方向分解系数 HH：对角线方向分解系数
     */
    public  List<Double[][]> mydwt2(double[][] _X,double[] lpd,double[] hpd){
        int M=_X.length;
        int N=_X[0].length;
        double[][] X=new double[M][N];
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++) X[i][j]=_X[i][j];
        }
        double[] row;
        List<Double> c;
        int index=0;
        List<List<Double>> rowTrans;
        List<Double[][]> res=new ArrayList<>();
        //对每一行序列进行一维离散小波分解
        for(int i=0;i<M;i++){
            row=Arrays.copyOf(X[i],N);
            rowTrans=mydwt(row,lpd,hpd,1);
            index=0;
            for(int j=0;j<rowTrans.size();j++){
                c=rowTrans.get(j);
                for(int k=0;k<c.size();k++){
                    X[i][index++]=c.get(k);
                }
            }
        }
        //对每一列序列进行一维离散小波分解
        double[] col=new double[M];
        for(int j=0;j<N;j++){
            for(int i=0;i<M;i++){
                col[i]=X[i][j];
            }
            rowTrans=mydwt(col,lpd,hpd,1);
            index=0;
            for(int k=0;k<rowTrans.size();k++){
                c=rowTrans.get(k);
                for(int i=0;i<c.size();i++){
                    X[index++][j]=c.get(i);
                }
            }
        }
        int M2=M/2;
        int N2=N/2;
        Double[][] LL=new Double[M2][N2];
        Double[][] LH=new Double[M2][N2];
        Double[][] HH=new Double[M2][N2];
        Double[][] HL=new Double[M2][N2];
        int x=0,y=0;
        //求解原矩阵左上角部分
        for(int i=0;i<M2;i++){
            y=0;
            for(int j=0;j<N2;j++){
                LL[x][y++]=X[i][j];
            }
            x++;
        }
        //求解原矩阵左下角部分
        x=0;
        for(int i=M2;i<M;i++){
            y=0;
            for(int j=0;j<N2;j++){
                LH[x][y++]=X[i][j];
            }
            x++;
        }
        //求解原矩阵右上角部分
        x=0;
        for(int i=0;i<M2;i++){
            y=0;
            for(int j=N2;j<N;j++){
                HH[x][y++]=X[i][j];
            }
            x++;
        }
        //求解原矩阵右下角部分
        x=0;
        for(int i=M2;i<M;i++){
            y=0;
            for(int j=N2;j<N;j++){
                HL[x][y++]=X[i][j];
            }
            x++;
        }
        // 垂直方向
        res.add(HL);
        // 对角线方向
        res.add(HH);
        // 水平方向
        res.add(LH);
        res.add(LL);
        return res;

    }

    public static void print(Double[][] X){
        int M=X.length;
        int N=X[0].length;
        System.out.println("------Start------");
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                System.out.print(" "+X[i][j]);
            }
            System.out.println();
        }
        System.out.println("------Over------");
    }
}
