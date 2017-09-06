package com.sgc.photo;

import com.sgc.geometric.ConnRegion;
import com.sgc.res.ExcisionUnit;
import com.sgc.service.ConnRegDet;
import com.sgc.service.Projection;
import com.sgc.util.Util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/8.
 */
public class PhotoExcision extends AbstractBufferedImageOp {
    public static double computeWMean(BufferedImage image) {
        // 获取连通区域
        ConnRegDet connRegDet = new ConnRegDet();
        List<ConnRegion> res = connRegDet.findConnRegs(image);
        // 打印连通区域信息
        int width = image.getWidth();
        int height = image.getHeight();
        // 获取图像数据
        int[] inPixels = new int[width * height];
        getRGB(image, 0, 0, width, height, inPixels);
        // 获取车牌垂直投影
        int[][] hv = Projection.getProjectionData(inPixels, width, height);

        // 中间部分坐标值
        int left = (int) (width * 37.0 / 100 + 0.5);
        int right = width - left;
        // 计算中间部分字符投影的最大值和次最大值
        int max1;
        max1 = 0;
        for (int col = left; col < right; col++) {
            if (max1 < hv[1][col]) {
                max1 = hv[1][col];
            }
        }
        int ch = (int) (max1  + 0.5);
//        System.out.println("字符高度:" + ch);
        double wLow = ch / 2.5;
        double wHigh = ch / 1.5;
        // 计算宽度平均值
        int wSum = 0;
        int k = 0;
        double wMean = 0;
        // 迭代连通区域
        Iterator<ConnRegion> ite = res.iterator();
        while (ite.hasNext()) {
            ConnRegion connRegion = ite.next();
            int cw = connRegion.getWidth();
            if (cw >= wLow && cw <= wHigh) {
                wSum += cw;
                k++;
            }
        }
        wMean = 1.0 * wSum / k;
//        System.out.println("字符平均宽度:" + wMean);
        // 精细分割阀值
        double wMin = (int) (wMean - 4);
        double wMax = (int) (wMean + 4);
//        System.out.println("精细分割宽度阀值:" + "wMin-" + wMin + "wMax-" + wMax);
        // 波谷坐标
        int min = Integer.MAX_VALUE;
        int troughIndex;
        // 精细分割结果集
        List<ConnRegion> objRes = new ArrayList<>();
        ite = res.iterator();
        while (ite.hasNext()) {
            ConnRegion connRegion = ite.next();
            int cw = connRegion.getWidth();
            int x = connRegion.getX();
            int y = connRegion.getY();
            if (cw >= wMin && cw <= wMax) {
                objRes.add(connRegion);
            } else if (cw > wMax) {// 计算该区域投影最小值
                min = Integer.MAX_VALUE;
                troughIndex = x;
                for (int col = x; col < x + cw; col++) {
                    if (min > hv[1][col]) {
                        min = hv[1][col];
                        troughIndex = col;
                    }
                }
                int nw1 = troughIndex - x;
                int nw2 = cw - nw1;
                if (nw1 >= wMin && nw1 <= wMax) {
                    connRegion.setWidth(nw1);
                    objRes.add(connRegion);
                }
                if (nw2 >= wMin && nw2 <= wMax) {
                    try {
                        ConnRegion temp = connRegion.clone();
                        temp.setWidth(nw2);
                        temp.setX(troughIndex);
                        objRes.add(temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//        System.out.println("结果集大小:" + objRes.size());
        // 更新字符宽度平均值
        wSum = 0;
        k = 0;
        ite = objRes.iterator();
        while (ite.hasNext()) {
            ConnRegion connRegion = ite.next();
            int cw = connRegion.getWidth();
            wSum += cw;
            k++;
        }
        wMean = 1.0 * wSum / k;
        return wMean;
    }

    public static double computeSPCMean(BufferedImage image) {
        // 获取字符平均宽度
        double wMean = computeWMean(image);
        // 获取图像数据
        int width = image.getWidth();
        int height = image.getHeight();
        int[] inPixels = new int[width * height];
        getRGB(image, 0, 0, width, height, inPixels);
        // 宽度阀值
        int wMin = (int) (wMean - 3.5);
        int wMax = (int) (wMean + 3.5);
        // 获取投影数据
        int[][] hv = Projection.getProjectionData(inPixels, width, height);
        // 间隔标记--表示间隔已经出现
        boolean flag = true;
        // 投影阀值
        int threshold = 0;
        // 点标记
        int[] mark = new int[width];
        Arrays.fill(mark, 0);
        for (int col = 0; col < width; col++) {
            if (!flag && threshold == hv[1][col]) {
                flag = true;
                mark[col] = 1;
            } else if (flag && threshold != hv[1][col]) {
                flag = false;
                mark[col] = 2;
            }
        }
        // 字符起止坐标
        int chStart = 0, chEnd = 0, lastEnd = 0;
        // 字符位置
        double index = 0;
        // 字符出现标记
        flag = false;
        // 计算平均间隔符
        double spcMean = 0;
        int spcSum = 0;
        int chw = 0;
        int K = 0;
        for (int col = 0; col < width; col++) {
            if (!flag && 2 == mark[col]) {
                flag = true;
                chStart = col;

            } else if (flag && 1 == mark[col]) {
                lastEnd = chEnd;
                chEnd = col;
                chw = chEnd - chStart;
                if (chw >= wMin && chw <= wMax) {
                    spcSum += (chStart - lastEnd);
                    K++;
                }
                flag = false;
            }
        }
        // 字符宽度及其间隔宽度
        spcMean = 1.0 * spcSum / K;
        return spcMean;
    }


    public List<ExcisionUnit> doExcision(BufferedImage image) {
        // 获取字符平均宽度
        double wMean = computeWMean(image);
        // 字符整数宽度
        int chw = (int) (wMean + 0.5);
        // 精细化分割阀值
        int wMin = (int) (wMean - 3.5);
        int wMax = (int) (wMean + 3.5);
        // 进行均值滤波除噪
        SmoothFilter smoothFilter = new SmoothFilter();
        BufferedImage temp = smoothFilter.filter(image, null);
        // 确定第2,3字符间隔位置
        // 获取待切分图像数据
        int width = temp.getWidth();
        int height = temp.getHeight();
        int[] inPixels = new int[width * height];
        getRGB(temp, 0, 0, width, height, inPixels);
        // 获取投影数据
        int[][] hv = Projection.getProjectionData(inPixels, width, height);
        // 扫描图像前半部分获取的最大字符间隔同时确认第二个字符结束位置
        int scanCol = (int) (width * 0.5 + 0.5);
        // 间隔标记
        boolean flag = true;
        // 当前扫描字符宽度及其间距
        int currStart, currEnd, lastEnd;
        // 记录最大间隔宽度
        int max = Integer.MIN_VALUE;
        int index1 = 0;
        currEnd = currStart = lastEnd = 0;
        for (int col = 0; col < scanCol; col++) {
            if (!flag && 0 == hv[1][col]) {
                flag = true;
                lastEnd = currEnd;
                currEnd = col;
                int spc = currStart - lastEnd;
                // 最大间隔的前一个字符的结尾(必然为字母)
                if (max < spc) {
                    index1 = lastEnd;
                    max = spc;
                }
            } else if (flag && 0 != hv[1][col]) {
                flag = false;
                currStart = col;
            }
        }
        List<ExcisionUnit> res = new ArrayList<>();
        ExcisionUnit[] excesions = new ExcisionUnit[7];
        //处理左边部分
        double L = 0;
        int K = 4;
        int count = 1;
        int[] output;
        int xz = (int) (index1 - 0.51 * height), xy = index1;
        while (count >= 0) {
            // 左边界处理
            xz = (int) (xy - 0.48 * height);
            xz=xz<0?0:xz;
            if (hv[1][xz] != L) {
                int i;
                for (i = 1; i <= K; i++) {
                    if ((xz - i)>0&&hv[1][xz - i] == L) {
                        xz -= i;
                        break;
                    }
                    if (hv[1][xz + i] == L) {
                        xz += i + 4;
                        break;
                    }
                }

            } else {
                for (int i = 0; i <= K; i++)
                    if (hv[1][xz + i] == L) {
                        xz += i;
                        break;
                    }
            }
            int cw = xy - xz + 1;
            output = new int[cw * height];
            getRGB(temp, xz, 0, cw, height, output);
            excesions[count] = new ExcisionUnit(xz, cw, height, output);
//            System.out.println("xz:" + xz + "xy" + xy + "cw:" + (xy - xz + 1));
            // 右边界计算
            xy = (int) (xz - 0.1 * height);
//            System.out.println("下一个字符初始右边界："+xy);
            xy=xy<0?0:xy;
            if (hv[1][xy] != L) {
                int i;
                for (i = 1; i <= K; i++) {
                    if (hv[1][xy + i] == L) {
                        xy += i;
                        break;
                    }
                    if ((xy - i)>0&&hv[1][xy - i] == L) {
                        xy -= i;
                        break;
                    }
                }
            }
//            System.out.println("下一个字符右边界:" + xy);
            count--;
        }
        // 垂直边框处理
        BorderDetFilter borderDetFilter = new BorderDetFilter();
        temp = borderDetFilter.dealVerBorder(image);
        // 获取处理边框后的图像
        width = temp.getWidth();
        height = temp.getHeight();
        inPixels = new int[width * height];
        getRGB(temp, 0, 0, width, height, inPixels);
        // 获取投影数据
        hv = Projection.getProjectionData(inPixels, width, height);
        // 获得垂直投影波峰波谷数据 -2:波峰  2:波谷
        int[] troughAndPeak = Util.markPeakAndTrough(hv[1]);
        // 扫描图像前半部分获取的最大字符间隔同时确认第二个字符结束位置
        scanCol = (int) (width * 0.5 + 0.5);
        // 间隔标记
        flag = true;
        // 处理右边部分
        int wLow=width/10;
        // 波谷位置
        int firstTrg, secondTrg;
        // 处理字符数
        count = 2;
        // 初始化访问标记
        flag = true;
        System.out.println("wMean="+wMean);
        for (int col = index1; col < width; col++) {
            if (!flag && 0 == hv[1][col]) {
                flag = true;
                currEnd = col;
                int cw = currEnd - currStart;
                if (cw <= wMax) {
                    output = new int[cw * height];
                    getRGB(temp, currStart, 0, cw, height, output);
                    excesions[count++] = new ExcisionUnit(currStart, cw, height, output);
                } else {
                    firstTrg = secondTrg = currStart;
                    // 如果当前投影区宽度大于精细化分割阀值，遍历波峰波谷标记数组
                    for (int i = currStart; i <= currEnd; i++) {
//                        System.out.println(String.format("%d,%d",i,troughAndPeak[i]));
                        if (i>=width-2||2 != troughAndPeak[i]) continue;
                        secondTrg = i + 1;

                        cw = secondTrg - firstTrg;
//                        System.out.println(String.format("%d,%d,w=%d",i,troughAndPeak[i],cw));
//                        System.out.println("cw="+cw+"wMean="+wMean);
                        if (Math.abs(cw/wMean-1)>0.16) continue;
                        output = new int[cw * height];
                        getRGB(temp, firstTrg, 0, cw, height, output);
                        excesions[count++] = new ExcisionUnit(firstTrg, cw, height, output);
                        firstTrg = secondTrg;
//                        System.out.println("firstTrg="+firstTrg);
                        if (count == excesions.length) break;
                    }
                }
                if (count == excesions.length) break;
            } else if (flag && 0 != hv[1][col]) {
                flag = false;
                currStart = col;
            }
        }
        int cw = currEnd - currStart;
        if (count < excesions.length) {
            firstTrg = secondTrg = currStart;
            // 如果当前投影区宽度大于精细化分割阀值，遍历波峰波谷标记数组
            for (int i = currStart - 1; i < width - 2; i++) {
//                System.out.println(String.format("%d,%d",i,troughAndPeak[i]));
                if (2 != troughAndPeak[i]) continue;
                secondTrg = i + 1;
                cw = secondTrg - firstTrg;
//                System.out.println(String.format("%d,%d,w=%d",i,troughAndPeak[i],cw));
                if (Math.abs(cw / wMean - 1) > 0.15) continue;
                currEnd = secondTrg;
                break;
            }
            if (currEnd < currStart) currEnd = width;
        }
        cw = currEnd - currStart;
        if (cw <= wMax && count != excesions.length) {
            output = new int[cw * height];
            getRGB(temp, currStart, 0, cw, height, output);
            excesions[count++] = new ExcisionUnit(currStart, cw, height, output);
        }
        for (ExcisionUnit exc : excesions) res.add(exc);
        return res;
    }
}