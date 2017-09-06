package com.sgc.geometric;

/**
 * Created by ChengXX on 2017/3/24.
 */
public class ConnRegion implements Comparable<ConnRegion>,Cloneable{
    private int x;
    private int y;
    private  int width;
    private int height;
    private double area;
    private double premeter;

    public ConnRegion(){
        this(0,0,0,0,0);
    }
    public ConnRegion(int x,int y,int width,int height,double area){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.area=area;
        this.premeter=2*(this.width+this.height);
    }

    @Override
    public int compareTo(ConnRegion o) {
        int flag=0;
        if(o.area>this.area) flag=1;
        else if(o.area<this.area) flag=-1;
        else{
            if(o.premeter>this.premeter) flag=1;
            else if(o.premeter<this.premeter) flag=-1;
        }
        return flag;
    }

    @Override
    public ConnRegion clone() throws CloneNotSupportedException {
        ConnRegion connRegion=(ConnRegion) super.clone();
        return connRegion;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPremeter() {
        return premeter;
    }

    public void setPremeter(double premeter) {
        this.premeter = premeter;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ConnRegion{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", area=" + area +
                ", premeter=" + premeter +
                '}';
    }
}
