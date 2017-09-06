package com.sgc.test;

import com.sgc.util.Util;

/**
 * Created by ChengXX on 2017/4/18.
 */
public class TroughPeakTest
{
    public static void main(String[] args){
        int[] data=new int[]{-5,10,10,14,14,8,8,6,6,-3,2,2,2,2,-3};
        int[] troughAndPeak= Util.markPeakAndTrough(data);
        for(int i=0;i<troughAndPeak.length;i++){
            System.out.print(String.format("  %d:%d",i,troughAndPeak[i]));
        }
    }
}
