package com.sgc.test;

/**
 * Created by ChengXX on 2017/3/14.
 */
public class RGBToLab {

    public static int[] LabTab=new int[1024];
    public static int Threshold=9;
    public static final int Shift=20;
    public static final  int HalfShiftValue=524288;
    public static final  int LABXBI=199049 ;
    public static final  int LABXGI =394494 ;
    public static final  int LABXRI =455033 ;
    public static final  int LABYBI =75675 ;
    public static final  int LABYGI =749900 ;
    public static final  int LABYRI =223002 ;
    public static final  int LABZBI =915161 ;
    public static final  int LABZGI =114795 ;
    public static  final int LABZRI =18621 ;
    public static  final int ScaleLC = (int)(16 * 2.55 * (1 << Shift) + 0.5);
   public static  final  int ScaleLT = (int)(116 * 2.55 + 0.5);
   public static final int Yn=95047;
    public static final int Xn= 100000;
    public static final int Zn=108883;






    public static void main(String[] args){
        double[] lab=RGB2Lab2(172,187,43);
        for(int i=0;i<lab.length;i++){
            System.out.print(String.format(" %.4f",lab[i]));
        }
        System.out.println();
        lab=RGB2Lab2(167,39,43);
        for(int i=0;i<lab.length;i++){
            System.out.print(String.format(" %.4f",lab[i]));
        }
        System.out.println();
        lab=RGB2Lab2(49,141,49);
        for(int i=0;i<lab.length;i++){
            System.out.print(String.format(" %.4f",lab[i]));
        }
        int Blue=43,Green=39,Red=167;
        int X,Y,Z,L,A,B;
        for (int I = 0; I < 1024; I++)
        {
            if (I > Threshold)
                LabTab[I] = (int)(Math.pow(1.0*I / 1020, 1.0F / 3) * (1 << Shift) + 0.5 );
            else
                LabTab[I] = (int)((29 * 29.0 * I / (6 * 6 * 3 * 1020) + 4.0 / 29) * (1 << Shift) + 0.5 );
        }
        X = (Blue * LABXBI + Green * LABXGI + Red * LABXRI + HalfShiftValue) >> (Shift - 2);//RGB->XYZ放大四倍后的结果
        Y = (Blue * LABYBI + Green * LABYGI + Red * LABYRI + HalfShiftValue) >> (Shift - 2);
        Z = (Blue * LABZBI + Green * LABZGI + Red * LABZRI + HalfShiftValue) >> (Shift - 2);
        X = LabTab[X];// 进行查表
        Y = LabTab[Y];
        Z = LabTab[Z];
        L = ((ScaleLT * Y - ScaleLC + HalfShiftValue) >>Shift);
        A = ((500 * (X - Y) + HalfShiftValue) >> Shift) + 128;
        B = ((200 * (Y - Z) + HalfShiftValue) >> Shift) + 128;
        System.out.println(""+X+" "+Y+" "+Z);
        System.out.println(String.format("L:%dA:%dB:%d",L,A,B));
    }
    static double[] RGB2Lab2(double R, double G, double B)
    {
        double[] Lab=new double[3];
        Lab[0] = 0.2126007 * R + 0.7151947 * G + 0.0722046 * B;
        Lab[1] = 0.3258962 * R - 0.4992596 * G + 0.1733409 * B + 128;
        Lab[2] = 0.1218128 * R + 0.3785610 * G - 0.5003738 * B + 128;
        return Lab;
    }
}
