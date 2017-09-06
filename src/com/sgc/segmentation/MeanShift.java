package com.sgc.segmentation;

import com.sgc.util.Util;
import com.sun.xml.internal.ws.util.UtilException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/8.
 */
public class MeanShift {
    private static final double C=1;
    private double bandwidth;

    public MeanShift(double bandwidth){
        this.bandwidth=bandwidth;
    }

    public MeanShift(){}
    //获取所有的漂移点
    public List<List<Double>> meanhift(List<List<Double>> points){
        //样本集规模
        int size=points.size();
        //访问标记数组
        List<Boolean> stop_moving=new ArrayList<>(size);
        //访问标记数组初始化
        for(int i=0;i<size;i++){
            stop_moving.add(false);
        }
        //漂移向量
        List<List<Double>> shifted_points= Util.copyLists(points);
        //最大漂移量
        double max_shift_distance=Double.MIN_VALUE;
        do {
            max_shift_distance=Double.MIN_VALUE;
            for (int i = 0; i < size; i++) {
                if (!stop_moving.get(i)) {
                    //如果当前样本未被访问则计算其漂移向量
                    List<Double> point_new = shift_point(shifted_points.get(i), points);
                    //计算本次漂移量
                    double shifted_distance = Util.euclidean_distance(shifted_points.get(i), point_new);
                    //保存当前最大漂移量
                    if (shifted_distance > max_shift_distance) max_shift_distance = shifted_distance;
                    //如果当前漂移量小于要求精度则将将其标注为已访问
                    if (shifted_distance <= EPSLION) stop_moving.set(i, true);
                    //保存样本i的最新漂移位置
                    shifted_points.set(i,point_new);
                }
            }
            //如果存在未达到精度的漂移量则继续进行漂移
        }while(max_shift_distance>EPSLION);
        return shifted_points;
    }
    public List<Cluster> cluster(List<List<Double>> points){
        List<List<Double>> shifted_points=meanhift(points);
        return cluster(points,shifted_points);
    }
    //get the next shift point
    private List<Double> shift_point(List<Double> point,List<List<Double>> points){
        List<Double> shifted_point=new ArrayList<>();
        int d=points.get(0).size();
        for (int dim=0;dim<d;dim++){
            shifted_point.add(0.0);
        }
        double total_weight=0;
        int size=points.size();
        for(int i=0;i<size;i++){
            List<Double> temp_point=points.get(i);
            //计算样本point到样本集中其他样本之间的距离
           double dis= Util.euclidean_distance(point,temp_point);
            //计算高斯权重
                double weight = dimensionsKernel(dis);
                //累加当前样本对漂移样本的贡献值
                for (int j = 0; j < d; j++) {
                    double shifted_weight = shifted_point.get(j);
                    shifted_weight += temp_point.get(j) * weight;
                    shifted_point.set(j, shifted_weight);
                }
                total_weight += weight;
            }
        for(int i=0;i<d;i++){
            double shifted_weight=shifted_point.get(i);
            shifted_weight/=total_weight;
            shifted_point.set(i,shifted_weight);
        }
        return shifted_point;
    }
    private List<Cluster> cluster(List<List<Double>> points, List<List<Double>> shifted_points){
        List<Cluster> clusters=new ArrayList<>();
        int shifted_point_size=shifted_points.size();
        for(int i=0;i<shifted_point_size;i++){
            int c=0;
            //内循环正常结束变量传的值均为clusters的大小
            for(;c<clusters.size();c++){
                //如果漂移点到某个聚类中心得距离小于聚类间的距离则结束循环
                //计算样本point到样本集中其他样本之间的距离
                List<Double> shift_point=shifted_points.get(i);
                List<Double> tempClus=clusters.get(c).mode;

                double dis=Util.euclidean_distance(shift_point,tempClus);

                if(dis<=CLUSTER_EPSLION) break;
            }
            //如果当前漂移点不属于已知的任何一个聚类中心，则将其视为新的聚类中心
            if(c==clusters.size()){
                Cluster clus=new Cluster();
                clus.mode=shifted_points.get(i);
                clusters.add(clus);
            }
            //如果当前点到聚类中心c的距离小于聚类中心间的距离，则将其分为类C
            clusters.get(c).original_points.add(points.get(i));
            clusters.get(c).shift_points.add(shifted_points.get(i));
        }
        List<Cluster> realClus=new ArrayList<>();
        List<List<Double>> needToAssign=new ArrayList<>();
        for(int i=0;i<clusters.size();i++){
            if(clusters.get(i).original_points.size()<=60){
                needToAssign.addAll(clusters.get(i).original_points);
            }else{
                realClus.add(clusters.get(i));
            }
        }
        return realClus;
    }

    public static double getBandwidth(List<List<Double>> points,double t){
        List<Double> c=new ArrayList<>();
        int d=points.get(0).size();
        int N=points.size();
        for(int i=0;i<d;i++){
            c.add(0.0);
        }
        for(int i=0;i<d;i++){
            for(int j=0;j<N;j++){
                double di=c.get(i);
                di+=points.get(j).get(i);
                c.set(i,di);
            }
        }
        for(int i=0;i<d;i++){
            double di=c.get(i);
            c.set(i,di/N);
        }
        double[] D=new double[N];
        for(int i=0;i<N;i++){
            D[i]=Util.euclidean_distance(points.get(i),c);
        }
        double _D=0;
        for(int i=0;i<N;i++){
            _D=_D+D[i];
        }
        _D/=N;
        return t*_D;
    }
    public List<Cluster> mean_shift_cluster(List<List<Double>> X,double bandwidth){
        int K=0;
        int D=X.get(0).size();
        double E=0.001*bandwidth;
        int BlockNum=X.size();
        int iteNum=BlockNum;
        boolean[] beenVisitedFlag=new boolean[BlockNum];
        Arrays.fill(beenVisitedFlag,false);
        List<int[]> clusterVotes=new ArrayList<>();
        List<Cluster> TempC=new ArrayList<>();
        List<Double> myMean=null;
        List<Double> myOldMean=null;
        List<Double> temp=null;
        boolean mergeWith=false;
        while(iteNum>0){
            //选取一个未被访问的数据项
            for(int i=0;i<BlockNum;i++){
                if(!beenVisitedFlag[i]){
                    myMean=Util.copyList(X.get(i));
                    break;
                }
            }
            int[] thisClusterVotes=new int[BlockNum];
            Arrays.fill(thisClusterVotes,0);
            Double[] sqDistToAll=new Double[BlockNum];
            Arrays.fill(sqDistToAll,0.0);
            double dist=Double.MAX_VALUE;
            int count=0;
            Cluster clus=new Cluster();
            while(true){
                for(int i=0;i<BlockNum;i++){
                    sqDistToAll[i]=Util.euclidean_distance(myMean,X.get(i));
                    if(sqDistToAll[i]<bandwidth){
                        thisClusterVotes[i]++;
                        beenVisitedFlag[i]=true;
                    }
                }
                myOldMean=Util.copyList(myMean);
                for(int i=0;i<myMean.size();i++){
                    myMean.set(i,0.0);
                }
                //计算新的聚类中心
                count=0;
                for(int i=0;i<BlockNum;i++){
                    if(sqDistToAll[i]<bandwidth){
                        count++;
                        temp=X.get(i);
                        for(int j=0;j<D;j++){
                            double di=myMean.get(j);
                            di+=temp.get(j);
                            myMean.set(j,di);
                        }
                    }
                }
                for(int i=0;i<D;i++){
                    double di=myMean.get(i);
                    di/=count;
                    myMean.set(i,di);
                }
                dist=Util.euclidean_distance(myMean,myOldMean);
                clus.original_points.add(myOldMean);
                clus.shift_points.add(myMean);
                if(dist<E){
                    int i=0;
                    mergeWith=false;
                    for(i=0;i<K;i++){
                        dist=Util.euclidean_distance(myMean,TempC.get(i).mode);
                        if(2*dist<bandwidth){
                            mergeWith=true;
                            break;
                        }
                    }
                    if(mergeWith){
                        int[] nearClusterVotes=clusterVotes.get(i);
                        for(int j=0;j<BlockNum;j++){
                            nearClusterVotes[j]+=thisClusterVotes[j];
                        }
                    }
                    else{
                        K+=1;
                        clus.mode=myMean;
                        TempC.add(clus);
                        clusterVotes.add(thisClusterVotes);
                    }
                    break;
                }
            }
            iteNum=0;
            for(int i=0;i<BlockNum;i++){
                if(!beenVisitedFlag[i]) iteNum++;
            }
        }
        int[][] clusVotes=new int[K][BlockNum];
        Double[][] C=new Double[K][D];
        for(int i=0;i<K;i++)
            Arrays.fill(C[i],0.0);
        int[] voters=new int[K];
        Arrays.fill(voters,0);
        clusterVotes.toArray(clusVotes);
        int max=Integer.MIN_VALUE;
        int ck=0;
        for(int i=0;i<BlockNum;i++){
            max=Integer.MIN_VALUE;
            for(int j=0;j<K;j++){
                if(max<clusVotes[j][i]){
                    max=clusVotes[j][i];
                    ck=j;
                }
            }
            voters[ck]++;
            for(int j=0;j<D;j++){
                C[ck][j]+=X.get(i).get(j);
            }
        }
        for(int i=0;i<K;i++){
            for(int j=0;j<D;j++){
                C[i][j]/=voters[i];
            }
            TempC.get(i).mode=Arrays.asList(C[i]);
        }
        return TempC;
    }

    private static double CLUSTER_EPSLION=12.5; //157s

    private static double EPSLION=0.008;


    private double dimensionsKernel(double xc){
        double h1=Util.gaussian_kernel(xc,bandwidth);
        return h1;
    }
}
