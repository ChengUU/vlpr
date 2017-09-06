package com.sgc.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/9.
 */
public class HarrTest {
    public static List<Double[][]> waveletTransformHaar(double[][] X){
        List<Double[][]> res=new ArrayList<>();
        int m,n,m2,n2,ik,ik1,jk,jk1;
        m=X.length;
        n=X[0].length;
        double[][] X0=new double[m+1][n+1];
        double a,b,c,d;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                X0[i][j]=X[i][j];
            }
        }
        for(int i=0;i<m+1;i++) X0[i][n]=X0[i][n-1];
        for(int j=0;j<n+1;j++) X0[m][j]=X0[m-1][j];
        m2=m/2;
        n2=n/2;
        if(2*m2<m) m2+=1;
        if(2*n2<n) n2+=1;

        Double[][] LL=new Double[m2][n2];
        Double[][] LH=new Double[m2][n2];
        Double[][] HH=new Double[m2][n2];
        Double[][] HL=new Double[m2][n2];
        ik=0;
        for(int i=0;i<m2;i++){
            ik1=ik+1;
            jk=0;
            for(int j=0;j<n2;j++){
                jk1=jk+1;
                a = X0[ik][jk];
                b = X0[ik][jk1];
                c = X0[ik1][jk];
                d = X0[ik1][jk1];
                LL[i][j] = (a + b + c + d) / 2;
                LH[i][j] = (a + b - c - d) / 2;
                HH[i][j] = (a - b + c - d) / 2;
                HL[i][j] = (a - b - c + d) / 2;

                jk += 2;
            }
            ik+=2;
        }
        res.add(HL);
        res.add(LH);
        res.add(HH);
        res.add(LL);
        return res;
    }

    public static void main(String[] args){
        double[][] L={{1,5,9,3},{4,2,8,1},{3,9,6,2},{4,2,5,8}};
        List<Double[][]> r1=waveletTransformHaar(L);
        int size=r1.size();
        for(int k=0;k<size;k++){
            Double[][] temp=r1.get(k);
            DWTTest.print(temp);
        }
        DWTTest t=new DWTTest();
        double[] lpd={1,1};
        double[] hpd={-1,1};
        List<Double[][]> r2=t.mydwt2(L,lpd,hpd);
        System.out.println();
        System.out.println();
        size=r2.size();
        for(int k=0;k<size;k++){
            Double[][] temp=r2.get(k);
            DWTTest.print(temp);
        }
    }
}
