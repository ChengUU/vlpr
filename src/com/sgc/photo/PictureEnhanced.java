package com.sgc.photo;

import com.sgc.geometric.ConnRegion;
import com.sgc.service.ConnRegDet;
import com.sgc.service.ObjRegDet;
import com.sgc.util.Util;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by ChengXX on 2017/4/26.
 */
public class PictureEnhanced extends AbstractBufferedImageOp {
    private int[][] sharpKernel=new int[][]{{0,-1,0},{-1,4,-1},{0,-1,0}};
    private int[][] sobelGradTransKernel=new int[][]{{-1,0,1},{-2,0,2},{-1,0,1}};
    private int[][] meanFilterKernel=new int[][]{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}};
    private int[][] meadianFilterkernel=new int[][]{{0,0,1,0,0},{0,0,1,0,0},{1,1,1,1,1},{0,0,1,0,0},{0,0,1,0,0}};

    public void setMeanFilterKernel(int[][] meanFilterKernel) {
        this.meanFilterKernel = meanFilterKernel;
    }

    public void setMeadianFilterkernel(int[][] meadianFilterkernel) {
        this.meadianFilterkernel = meadianFilterkernel;
    }

    private double[] LogarithmTable=new double[256];
    // 均值滤波

    // 中值滤波
    public BufferedImage meadianFilter(BufferedImage image){
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];

        int rRadius=meadianFilterkernel.length/2;
        int cRadius=meadianFilterkernel[0].length/2;

        int index,index2;
        Integer[][] matrix=new Integer[3][];
        List<Integer> trs=new ArrayList<>();
        List<Integer> tgs=new ArrayList<>();
        List<Integer> tbs=new ArrayList<>();
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                int rowoffset,coloffset;
                for(int rows=-rRadius;rows<=rRadius;rows++){
                    rowoffset=row+rows;
                    if(rowoffset<0||rowoffset>=height) rowoffset=row-rows;
                    for(int cols=-cRadius;cols<=cRadius;cols++){
                        coloffset=col+cols;
                        if(coloffset<0||coloffset>=width) coloffset=col-cols;
                        if(0==meadianFilterkernel[rRadius+rows][cRadius+cols]) continue;
                        index2=rowoffset*width+coloffset;
                        trs.add((inPixels[index2]>>16)&0xff);
                        tgs.add((inPixels[index2]>>8)&0xff);
                        tbs.add(inPixels[index2]&0xff);
                    }
                }
                int size=trs.size();
                matrix[0]=trs.toArray(new Integer[size]);
                matrix[1]=tgs.toArray(new Integer[size]);
                matrix[2]=tbs.toArray(new Integer[size]);
                trs.clear();
                tgs.clear();
                tbs.clear();
                int[] rgb=performFIlter(matrix);
                int ia=(inPixels[index]>>24)&0xff;
                int ir=rgb[0];
                int ig=rgb[1];
                int ib=rgb[2];
                output[index]=(ia<<24)|(ir<<16)|(ig<<8)|ib;
            }
        }
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }

    private int[] performFIlter(Integer[][] matrix){
        int[] rgb=new int[3];
        Integer[] trs=matrix[0];
        Integer[] tgs=matrix[1];
        Integer[] tbs=matrix[2];
        Arrays.sort(trs);
        Arrays.sort(tgs);
        Arrays.sort(tbs);
        int count=trs.length;
        rgb[0]=trs[count/2];
        rgb[1]=tgs[count/2];
        rgb[2]=tgs[count/2];
        return rgb;
    }

    public BufferedImage enhance(BufferedImage image){
        PhotoClassifier photoClassifier=PhotoClassifier.getInstance();
        int type=photoClassifier.classify(image);
        System.out.println();System.out.println(PhotoClassifier.PHOTO_TYPE[type]);
        // 图像灰度化
        GrayFilter grayFilter=new GrayFilter();
        BufferedImage dest=grayFilter.filter(image,null);
        switch (type){
            case PhotoClassifier.DAYTIME_BLAZE:dest=enhanceDTB(dest);break;
            case PhotoClassifier.DAYTIME_LOW_LIGHT:dest=enhanceDTLL(dest);break;
            case PhotoClassifier.NIGHT_BLAZE:dest=enhanceNB(dest);break;
            case PhotoClassifier.NIGHT_LOW_LIGHT:dest=enhanceNLL(dest,1);break;
        }
        // 中值滤波
        dest=meadianFilter(dest);
        BilateralFilter bilateralFilter=new BilateralFilter();
        if(type==PhotoClassifier.DAYTIME_NORMAL||type==PhotoClassifier.NIGHT_NORMAL||type== PhotoClassifier.DAYTIME_LOW_LIGHT)
            dest=meanSmooth(dest);
        else dest=bilateralFilter.filter(dest,null);
        return dest;
    }
    // 白天强光型
    public BufferedImage enhanceDTB(BufferedImage image){
        BufferedImage dest=createCompatibleDestImage(image,null);
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        // Step 1:进行聚类操作，将图像划分为三个亮度区域[0,t1](t1,t2)[t2,L-1]
        PhotoClassifier photoClassifier=PhotoClassifier.getInstance();
        double[] lmh=photoClassifier.computeLightenessLM(image,5);
        // 计算对数变换查找表
        generateLogTableDayTimeBlaze(lmh[0],lmh[2],lmh[3]);
        int index,tr,ta;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                tr=(inPixels[index]>>16)&0xff;
                tr=(int)LogarithmTable[tr];
                output[index]=(ta<<24)|(tr<<16)|(tr<<8)|tr;
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public void generateLogTableDayTimeBlaze(double t1,double t2,double t3){
        int len=LogarithmTable.length;
        double a,b;
        for(int i=0;i<len;i++){
            if(i<t1) LogarithmTable[i]=0;
            else if(i>=t3){
                a=(i-t2)/(255-t2);
                a=computeGamaVal(i,a);
                b=1-t2/255;
                LogarithmTable[i]=t2+a*b;
            }else{
                a=(t1-t2)/(255-t2);
                a=computeGamaVal(i,a)*(1-t2/255)+t2;
                LogarithmTable[i]=a*(i-t1)/(t3-t1);
            }
        }
    }
    public double computeGamaVal(double i,double x){
        double gd= Util.computeGamaD(i,2.5);
        double gb=Util.computeGamaB(i,2.5);
        return x*gd+(1-x)*gb;
    }
    // 白天弱光型
    public BufferedImage enhanceDTLL(BufferedImage image){
        BufferedImage dest=createCompatibleDestImage(image,null);
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        // Step 1:进行聚类操作，将图像划分为三个亮度区域[0,t1](t1,t2)[t2,L-1]
        PhotoClassifier photoClassifier=PhotoClassifier.getInstance();
        double[] lmh=photoClassifier.computeLightenessLM(image,5);
        // 计算对数变换查找表
        generateLogTable(lmh[1],lmh[3],lmh[4]);
        int index,tr,ta;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                tr=(inPixels[index]>>16)&0xff;
                tr=(int)LogarithmTable[tr];
                output[index]=(ta<<24)|(tr<<16)|(tr<<8)|tr;
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    private void generateLogTable(double t1,double t2,double t3){
        System.out.println("t1="+t1+"t2="+t2+"t3="+t3);
        int len=LogarithmTable.length;
        for(int i=0;i<len;i++){
            if(i<t1) LogarithmTable[i]=0;
            else if(i>t2) LogarithmTable[i]=255;
            else {
                LogarithmTable[i]=t3*(Math.log(i+1)/Math.log(t2+1))-t1;
//                System.out.println("LogarithmTable[i]="+LogarithmTable[i]);
//                System.out.println((Math.log(i)-lnt1)/(lnt2-lnt1));
            }
        }
    }
    // 夜间强光型
    public BufferedImage enhanceNB(BufferedImage image){
        BufferedImage dest=createCompatibleDestImage(image,null);
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        int[] output=new int[width*height];
        Arrays.fill(output,-16777216);
        // Step 1:进行聚类操作，将图像划分为三个亮度区域[0,t1](t1,t2)[t2,L-1]
        PhotoClassifier photoClassifier=PhotoClassifier.getInstance();
        double[] lmh=photoClassifier.computeLightenessLM(image,3);
        // Step 2:图像二值化操作获取候选区域
        double threshold=lmh[1];
        int index,ta,tr;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                tr=(inPixels[index]>>16)&0xff;
                if(tr>threshold) output[index]=-1;
            }
        }
        setRGB(dest,0,0,width,height,output);
        ConnRegDet connRegDet=new ConnRegDet();
        List<ConnRegion> temp=connRegDet.findConnRegs(dest);
        System.out.println("二值图像连通区域数="+temp.size());
        // 连通区域物理特征判断
        ConnRegion connRegion=null;
        Collections.sort(temp);
        Iterator<ConnRegion> ite=temp.iterator();
        while(ite.hasNext()) {
            connRegion = ite.next();
            double wh=connRegion.getWidth()/connRegion.getHeight();
            double e = 22 * connRegion.getArea() / (connRegion.getPremeter() * connRegion.getPremeter());
            if (e < 0.9 || e > 1.25 || connRegion.getArea() < 1200) ite.remove();
        }
        System.out.println("连通区域筛选结果="+temp.size());
        //Step 3: 对候选区域进行对数变换
        int connw,connh,x,y;
        int[] connoutput;
        getRGB(image,0,0,width,height,inPixels);
        setRGB(dest,0,0,width,height,inPixels);
        ObjRegDet objRegDet=new ObjRegDet();
        ite=temp.iterator();
        while(ite.hasNext()) {
            connRegion = ite.next();
            BufferedImage obj=objRegDet.getObjReg(image,connRegion);
            lmh=photoClassifier.computeLightenessLM(obj,3);
            generateLogarithmTable(lmh[0],lmh[1]);
            x=connRegion.getX();
            y=connRegion.getY();
            connw=connRegion.getWidth();
            connh=connRegion.getHeight();
            connoutput=new int[connw*connh];
            getRGB(obj,0,0,connw,connh,connoutput);
            for(int row=0;row<connh;row++){
                for(int col=0;col<connw;col++){
                    index=row*connw+col;
                    ta=(connoutput[index]>>24)&0xff;
                    tr=(connoutput[index]>>16)&0xff;
                    tr=(int)LogarithmTable[tr];
                    connoutput[index]=(ta<<24)|(tr<<16)|(tr<<8)|tr;
                }
            }
            setRGB(dest,x,y,connw,connh,connoutput);
        }
        return dest;
    }
    // 计算对数变换查找表
    public void generateLogarithmTable(double t1,double t2){
        double delta=0;
        if(t2>=200) delta=0;
        else delta=181.5-0.825*t2;
        int len=LogarithmTable.length;
        for(int i=0;i<len;i++){
            if(i<t1) LogarithmTable[i]=0;
            else if(i>t2) LogarithmTable[i]=255;
            else{
                LogarithmTable[i]=i*(Math.log(delta+256)-Math.log(delta+1))/255+Math.log(delta+1);
            }
        }
    }
    // 夜间弱光类型锐化操作
    public BufferedImage sharp(BufferedImage image,int a){
        BufferedImage dest=createCompatibleDestImage(image,null);
        // 获取原图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 修改模板中心系数
        int kh=sharpKernel.length;
        int kc=sharpKernel[0].length;
        sharpKernel[kh/2][kc/2]+=a;
        // 输出图像数据
        int[] output=calculate(inPixels,width,height,sharpKernel);
        int e=a/Math.abs(a);
        int ta,r,g,b,ra,rb,ga,gb,ba,bb;
        int index;
        for(int row=0;row<height;row++) {
            for (int col = 0; col < width; col++) {
                index=row*width+col;
                ta = (inPixels[index] >> 24) & 0xff;
                ra = (inPixels[index] >> 16) & 0xff;
                rb = (output[index] >> 16) & 0xff;
                r = clamp(a*ra +  e*rb);
                ga = (inPixels[index] >> 8) & 0xff;
                gb = (output[index] >> 8) & 0xff;
                g = clamp(a*ga +  e*gb);
                ba = inPixels[index]  & 0xff;
                bb = output[index]  & 0xff;
                b = clamp(a*ba +  e*bb);
                output[index] = (ta << 24) | (r << 16) | (g << 8) | b;
            }
        }
        setRGB(dest,0,0,width,height,output);
        return dest;

    }
    public BufferedImage sobelGradTransform(BufferedImage image){
        BufferedImage dest=createCompatibleDestImage(image,null);
        // 获取原图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        // 输出图像数据
        int[] output=calculate(inPixels,width,height,sobelGradTransKernel);

        output=meanSmooth(output,width,height,meanFilterKernel);
        setRGB(dest,0,0,width,height,output);
        return dest;
    }
    public BufferedImage enhanceNLL(BufferedImage image,int a){
        BufferedImage dest=createCompatibleDestImage(image,null);
        BufferedImage sharpImage=sharp(image,a);
        BufferedImage gradImage=sobelGradTransform(image);
        // 获取图像数据
        int width=image.getWidth();
        int height=image.getHeight();
        int[] imgA=new int[width*height];
        getRGB(sharpImage,0,0,width,height,imgA);
        int[] imgB=new int[width*height];
        getRGB(gradImage,0,0,width,height,imgB);
        int[] output=new int[width*height];
        // multiply image A and B
        int index,ta,ra,rb,r;
        // 反掩蔽运算锐化图
        // 检查灰度值的最大最小值
        int min=Integer.MAX_VALUE;
        int max=Integer.MIN_VALUE;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ra=(imgA[index]>>16)&0xff;
                rb=(imgB[index]>>16)&0xff;
                r=ra*rb;
                if(r<min) min=r;
                if(r>max) max=r;
                output[index]=r;
            }
        }
        int l=max-min+1;
        int[] his=new int[l];
        long count=0;
        Arrays.fill(his,0);
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                r=output[index];
                his[r-min]++;
                count++;
            }
        }
        int sum=0;
        int t=l-1;
        for(int i=l-1;i>=0;i--){
            sum+=his[i];
            if(1.0*sum/count>0.01){
                t=i+min;
                break;
            }
        }
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                r=output[index];
                if(r<=0) r=0;
                else if(r>=t) r=255;
                else {
                    r=r*255/t;
                }
                r=clamp(r);
                output[index]=(255<<24)|(r<<16)|(r<<8)|r;
            }
        }
        setRGB(dest,0,0,width,height,output);

        return dest;
    }

    public BufferedImage meanSmooth(BufferedImage image){
        int width=image.getWidth();
        int height=image.getHeight();
        int[] inPixels=new int[width*height];
        getRGB(image,0,0,width,height,inPixels);
        meanFilterKernel=new int[][]{{1,2,1},{2,4,2},{1,2,1}};
        inPixels=meanSmooth(inPixels,width,height,meanFilterKernel);
        BufferedImage dest=createCompatibleDestImage(image,null);
        setRGB(dest,0,0,width,height,inPixels);
        return dest;
    }

    private int[] meanSmooth(int[] inPixels,int width,int height,int[][] kernel){
        int[] output=new int[width*height];
        // 拉普拉斯核大小
        double len=0;
        int rRadius=kernel.length/2;
        int cRadius=kernel[0].length/2;
        int ta,tr,tg,tb,index,index2;
        // 迭代坐标
        int nrow,ncol;
        int npixel=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                index=row*width+col;
                ta=(inPixels[index]>>24)&0xff;
                tr=tg=tb=0;
                len=0;
                for (int rows = -rRadius; rows <= rRadius; rows++) {
                    nrow = row + rows;
                    if (nrow < 0 || nrow >= height) nrow = row - rows;
                    for (int cols = -cRadius; cols <= cRadius; cols++) {
                        ncol = col + cols;
                        if (ncol < 0 || ncol >= width) ncol = col - cols;
                        int[] rgb=getPixel(inPixels,width,height,ncol,nrow);
                        tr+=rgb[0]*kernel[rRadius+rows][cRadius+cols];
                        tg+=rgb[1]*kernel[rRadius+rows][cRadius+cols];
                        tb+=rgb[2]*kernel[rRadius+rows][cRadius+cols];
                        len+=kernel[rRadius+rows][cRadius+cols];
                    }
                }
                tr=(int)Math.floor(tr/len);
                tg=(int)Math.floor(tg/len);
                tb=(int)Math.floor(tb/len);
                tr = clamp(tr);
                tg = clamp(tg);
                tb = clamp(tb);

                output[index]=(ta<<24)|(tr<<16)|(tg<<8)|tb;
            }
        }
        return output;
    }

    private int[] calculate(int[] inPixels,int width,int height,int[][] kerlnel){
        int[] output=new int[width*height];
        // 拉普拉斯核大小
        int rRadius=kerlnel.length/2;
        int cRadius=kerlnel[0].length/2;
        int ta,tr,tg,tb,index,index2;
        // 迭代坐标
        int nrow,ncol;
        int npixel=0;
        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr=tg=tb=0;
                for (int rows = -rRadius; rows <= rRadius; rows++) {
                    nrow = row + rows;
                    if (nrow < 0 || nrow >= height) nrow = row - rows;
                    for (int cols = -cRadius; cols <= cRadius; cols++) {
                        ncol = col + cols;
                        if (ncol < 0 || ncol >= width) ncol = col - cols;
                        int[] rgb=getPixel(inPixels,width,height,ncol,nrow);
                        tr+=rgb[0]*kerlnel[rRadius+rows][cRadius+cols];
                        tg+=rgb[1]*kerlnel[rRadius+rows][cRadius+cols];
                        tb+=rgb[2]*kerlnel[rRadius+rows][cRadius+cols];
                    }
                }
                tr = clamp(tr);
                tg = clamp(tg);
                tb = clamp(tb);
                output[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        return output;
    }

    private int[] getPixel(int[] inPixels, int width, int height, int col, int row) {
        if(col < 0 || col >= width)
            col = 0;
        if(row < 0 || row >= height)
            row = 0;
        int index = row * width + col;
        int tr = (inPixels[index] >> 16) & 0xff;
        int tg = (inPixels[index] >> 8) & 0xff;
        int tb = inPixels[index] & 0xff;
        return new int[]{tr, tg, tb};
    }
}
