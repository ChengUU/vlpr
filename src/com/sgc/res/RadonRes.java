package com.sgc.res;


/**
 * Created by ChengXX on 2017/3/30.
 */
public class RadonRes {
    private final double[] pPtr;
    private final int[] xp;
    private final double[] theta;

    public RadonRes(double[] pPtr,int[] xp,double[] theta){
        this.pPtr=pPtr;
        this.xp=xp;
        this.theta=theta;
    }

    public double[] getpPtr() {
        return pPtr;
    }

    public int[] getXp() {
        return xp;
    }

    public double[] getTheta() {
        return theta;
    }

    /**
     * get the angle that relative to vertical
     * @return
     */
    public double getDipAngle(){
        int rSize=xp.length;
        int numAngles=theta.length;
        double max=Double.MIN_VALUE;
        int index=0;
        for(int i=0;i<numAngles;i++){
            for(int j=0;j<rSize;j++){
                if(pPtr[i*rSize+j]>max){
                    max=pPtr[i*rSize+j];
                    index=i;
                }
            }
        }
        return theta[index];
    }
}
