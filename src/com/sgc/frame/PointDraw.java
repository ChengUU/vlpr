package com.sgc.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/9.
 */
public class PointDraw extends JPanel{
    private static int WIDTH=4;
    private static int HEIGHT=4;
    private List<List<Double>> points;
    private List<List<Double>> clusters;
    private double[][] U;
    public static int SCREEN_WIDTH=700;
    public static int SCREEN_HEIGHT=400;
    private static Color[] Colors={Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.PINK, Color.BLUE,Color.MAGENTA};
    private static int COLOR_LENGTH=7;

    private Image buffImg;

    public PointDraw(List<List<Double>> points,List<List<Double>> clusters,double[][] U){
        this.clusters=clusters;
        this.points=points;
        this.U=U;
    }

    public List<List<Double>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Double>> points) {
        this.points = points;
    }

    public List<List<Double>> getClusters() {
        return clusters;
    }

    public void setClusters(List<List<Double>> clusters) {
        this.clusters = clusters;
    }

    public double[][] getU() {
        return U;
    }

    public void setU(double[][] u) {
        U = u;
    }

    private void graphicCluster(Graphics g,List<List<Double>> clusters){
        int size=clusters.size();
        for(int i=0;i<size;i++){
            List<Double> point=clusters.get(i);
            graphicSample(g,point,Color.BLACK);
        }
    }
    private void graphicData(Graphics g,List<List<Double>> data,double[][] U){
        double min=999;
        int index=0;
        for(int i=0;i<data.size();i++){
            min=999;
            for(int j=0;j<clusters.size();j++){
                if(min>U[j][i]){
                    min=U[j][i];
                    index=j;
                }
            }
            graphicSample(g,data.get(i),Colors[index%COLOR_LENGTH]);
        }
    }

    private void graphicSample(Graphics g,List<Double> sample,Color color){
        Graphics2D g2=(Graphics2D)g;
        g.setColor(color);
        double x=sample.get(0);
        double y=sample.get(1);
        Rectangle2D rectangle2D=new Rectangle2D.Double(x,y,WIDTH,HEIGHT);
        Ellipse2D ellipse2D=new Ellipse2D.Double();
        ellipse2D.setFrame(rectangle2D);
        g2.draw(ellipse2D);
        g2.fill(ellipse2D);
    }
    public void paint(Graphics g){
        graphicData(g,points,U);
        graphicCluster(g,clusters);
    }

    public void update(Graphics g){
        buffImg=this.createImage(WIDTH,HEIGHT);
        Graphics bg=buffImg.getGraphics();
        bg.setColor(Color.WHITE);
        bg.fillRect(0,0,WIDTH,HEIGHT);
        paint(bg);
        g.drawImage(buffImg,0,0,this);
    }
}
