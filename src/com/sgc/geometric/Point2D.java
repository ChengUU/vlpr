package com.sgc.geometric;

/**
 * Created by ChengXX on 2017/3/23.
 */
public class Point2D {
    private int x;
    private int y;

    public Point2D(){this(0,0);}
    public Point2D(int x,int y){

        this.x=x;
        this.y=y;
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

    public boolean equals(Object otherObject){
        if(this==otherObject) return true;
        if(null==this) return false;
        if(this.getClass()!=otherObject.getClass()) return false;
        Point2D other=(Point2D)otherObject;
        return this.x==other.x&&this.y==other.y;

    }

    public int hashCode(){
        int hash=0;
        hash+=7*Integer.hashCode(x)+11*Integer.hashCode(y);
        return hash;
    }


    public static  Point2D add(Point2D p1,Point2D p2){
        int x=p1.x+p2.x;
        int y=p1.y+p2.y;
        return new Point2D(x,y);
    }

    public String toString(){
        StringBuffer strb=new StringBuffer();
        strb.append("Point2D[x=");
        strb.append(this.x+",y=");
        strb.append(this.y+"]");
        return strb.toString();
    }
}
