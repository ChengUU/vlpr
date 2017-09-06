package com.sgc.service;

import com.sgc.geometric.ConnRegion;
import com.sgc.photo.BinaryFilter;
import com.sgc.photo.GrayFilter;
import com.sgc.photo.HistogramFIlter;
import com.sgc.util.Util;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by ChengXX on 2017/4/13.
 */
public class FacticityValidator {

    public final static int W_MIN=45;
    public final static int W_MAX=185;
    public final static int H_MIN=9;
    public final static int H_MAX=60;
    private List<List<ConnRegion>> adjoinPairs=new ArrayList<>();


    public List<ConnRegion> validate(BufferedImage sourceImage,List<ConnRegion> connRegs){
        int size=connRegs.size();
        if(1==size) return connRegs;
        // 图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        //图像二值化
        BinaryFilter binaryFilter=new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        //直方图
        HistogramFIlter histogramFIlter=new HistogramFIlter();
        //投影操作
        Projection projection=new Projection();
        double hi=0;
        double varSum=0;
        List<List<Double>> pi=new ArrayList<>();
        Iterator<ConnRegion> ite=connRegs.iterator();
        while(ite.hasNext()) {
            ConnRegion connRegion = ite.next();
//            System.out.println(connRegion);
            ObjRegDet objRegDet = new ObjRegDet();
            BufferedImage sample = objRegDet.getObjReg(sourceImage, connRegion);
            BufferedImage gray = grayFilter.filter(sample, null);
            List<Double> p = new ArrayList<>();
            double[] pVal=new double[5];
            int width = connRegion.getWidth();
            int height = connRegion.getHeight();
            // 候选区域长宽比
            if (width >= W_MIN && width <= W_MAX && height >= H_MIN && height <= H_MAX) hi = 1.0 * width / height;
            else hi = 0;
            if (hi >= 2 && hi <= 6.5) pVal[0] = 1.0 / (1 + (hi - 4) * (hi - 4));
            else {ite.remove();System.out.println("hi="+hi);continue;}
            // 区域二值图像水平跳变数
            pVal[1] = histogramFIlter.jumpLine(gray);
            if(0==pVal[1]){ite.remove();System.out.println(" pVal[1]="+ pVal[1]);continue;}
            // 候选区域字符所占比例
            pVal[2]=binaryFilter.occupancy(sample);
            if(pVal[2]>=0.1&&pVal[2]<=0.8) pVal[2]=1/(1+100*(pVal[2]-0.35)*(pVal[2]-0.35));
            else {ite.remove();System.out.println("pVal[2]="+ pVal[2]);continue;}
            // 垂直投影峰谷跳变
            pVal[4]=projection.troughJumpCount(gray);
            if( pVal[4]>=6&& pVal[4]<=30)  pVal[4]=100.0/(100+(( pVal[4]-8)*( pVal[4]-8))+( pVal[4]-20)*( pVal[4]-20));
            else {ite.remove(); System.out.println("pVal[4]="+ pVal[4]);continue;}
            // 候选区域灰度方差
            pVal[3]=grayFilter.computeVariance(sample);
            varSum+=pVal[3];
            for(double value:pVal) p.add(value);
            pi.add(p);
        }
        // 更新灰度方差
        size= pi.size();
        double varMean=varSum/size;
        int i=0;
        Iterator<List<Double>> pIte= pi.iterator();
        while(pIte.hasNext()) {
            List<Double> ptmp = pIte.next();
            double p3 = ptmp.get(3);
            if(p3<varMean)
                p3 = 1 - p3 / varMean;
            else if(p3>varMean) p3=0;
            ptmp.set(3, p3);
            i++;
        }
        size=connRegs.size();
        List<ConnRegion> objList=new ArrayList<>();
        pIte=pi.iterator();
        i=0;
        while(pIte.hasNext()) {
            List<Double> ptmp = pIte.next();
            Double[] pVal=ptmp.toArray(new Double[5]);
            System.out.println("pVal[0]="+pVal[0]+"pVal[1]="+pVal[1]+"pVal[2]="+pVal[2]+"pVal[3]="+pVal[3]+"pVal[4]="+pVal[4]);
            double coe=0.3*pVal[0]+0.2*pVal[1]+0.15*pVal[2]+0.2*pVal[3]+0.15*pVal[4];
            System.out.println("coe="+coe);
            if(coe>=0.55) {
                ConnRegion connRegion=connRegs.get(i);
                double width=connRegion.getWidth();
                double height=connRegion.getHeight();
                double wh=width/height;
                int deltaY=0;
                System.out.print("wh="+wh);
                if(wh>=3.2){
                    deltaY=(int)(height*0.2);
                    height=height*1.4;
                    int y=connRegion.getY();
                    y=y-deltaY;
                    connRegion.setHeight((int)height);
                    connRegion.setY(y);
                }
                objList.add(connRegs.get(i));
            }
//            System.out.println("综合隶属度="+coe);
            i++;
        }
        return objList;
    }

    public boolean validate(List<ConnRegion> connRegs){
        if(null==connRegs) throw new IllegalArgumentException("The connection results must be a real set.");
        // 去除异常连通区域
//        precondition(connRegs);
        System.out.println(connRegs);
        // 连通区域数
        int m=connRegs.size();
        // 广度搜索访问标记
        boolean[] flag=new boolean[m];
        Arrays.fill(flag,false);
        // 计算外接矩形中心和平均高度
        int hSum=0;
        int wSum=0;
        for(int i=0;i<m;i++){
            ConnRegion connRegion=connRegs.get(i);
            int x=connRegion.getX()+connRegion.getWidth()/2;
            int y=connRegion.getY()+connRegion.getHeight()/2;
            hSum+=connRegion.getHeight();
            wSum+=connRegion.getWidth();
            connRegion.setX(x);
            connRegion.setY(y);
        }
        double hMean=1.0*hSum/m;
        double wMean=1.0*wSum/m;
        // 高度上下阀值
        double ha=1.2*hMean;
        double hb=6.6*hMean;
        System.out.println(String.format("ha-hb:%.2f-%.2f",ha,hb));
        // 宽度上下阀值
        double wa=1.1*wMean;
        double wb=6.6*wMean;
        System.out.println(String.format("wa-wb:%.2f-%.2f",wa,wb));
        // 角度阀值
        double angleThreshold=Math.PI/6;

        // 类似图的搜索--广度搜索
        // 搜索队列
        // 访问节点
        ConnRegion current,next;
        int cx,cy;
        double ch,cw;
        int nx,ny;
        double nh,nw;
        double deltaX,deltaY;
        double ratioH,ratioW;
        double angle;
        double variance;
        List<Double> angles=new ArrayList<>();
        // 邻接链及其长度
        List<ConnRegion> res=new ArrayList<>();
        int len=0;
        Queue<ConnRegion> que=new LinkedList();
        for(int i=0;i<m;i++){
            if(!flag[i]){
                flag[i]=true;
                res=new ArrayList<>();
                que.offer(connRegs.get(i));
                // 开始搜索
                angles.clear();
                while(!que.isEmpty()){
                    len++;
                    current=que.poll();
                    res.add(current);
                    cx=current.getX();
                    cy=current.getY();
                    ch=current.getHeight();
                    cw=current.getWidth();
                    for(int j=0;j<m;j++){
                        if(!flag[j]){// 访问条件
                            next=connRegs.get(j);
                            nx=next.getX();
                            ny=next.getY();
                            nh=next.getHeight();
                            nw=next.getWidth();
                            deltaX=Math.abs(nx-cx);
                            if(deltaX>wb||deltaX<wa){
//                                System.out.println("deltaX:"+deltaX);
//                                System.out.println("间距检测未通过");
                                continue;
                            }
                            deltaY=Math.abs(ny-cy);
                            ratioH=Math.abs(ch/nh-1.0);
                            if(ratioH>0.1) {
//                                System.out.println("ratioH:"+ratioH);
//                                System.out.println("高度相近性检测未通过");
                                continue;
                            }
                            ratioW=Math.abs(cw/nw-1.0);
                            if(ratioW>0.1) {
//                                System.out.println("ratioW:"+ratioW);
//                                System.out.println("宽度相近性检测未通过");
                                continue;
                            }
                            angle=Math.atan(deltaY/deltaX);
                            if(angle>angleThreshold) {
//                                System.out.println("angle:"+angle);
//                                System.out.println("角度检测未通过");
                                continue;
                            }
                            angles.add(angle);
                            flag[j]=true;
                            que.offer(connRegs.get(j));
                        }
                    }
                }
                variance= Util.computeVariance(angles);
                len=res.size();
                if(len>=2) {
                    adjoinPairs.add(res);
//                    System.out.println(res);
//                    System.out.println("邻接链长度:"+len);
//                    System.out.println("角度方差:"+variance);
//                    System.out.println("角度值:"+angles);
                }
            }
        }
        return adjoinPairs.size()>0;
    }

    public void precondition(List<ConnRegion> connRegions){
        Iterator<ConnRegion> ite=connRegions.iterator();
        double width,height;
        double ratioWH;
        ConnRegion connRegion=null;
        while(ite.hasNext()){
            connRegion=ite.next();
            width=connRegion.getWidth();
            height=connRegion.getHeight();
            ratioWH=width/height;
            if(ratioWH>0.575||ratioWH<0.425) ite.remove();
        }
    }
}
