package com.sgc.segmentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengXX on 2017/3/8.
 */
public class Cluster {
        public List<Double> mode=new ArrayList<Double>();
        public List<List<Double>> original_points=new ArrayList<List<Double>>();
        public List<List<Double>> shift_points=new ArrayList<List<Double>>();
        public String toString(){
                StringBuffer strbuff=new StringBuffer();
                strbuff.append("[mode=");
                strbuff.append(mode);
                strbuff.append(",original_points=");
                strbuff.append(original_points);
                strbuff.append(",shift_points=");
                strbuff.append(shift_points);
                strbuff.append("]");
                return strbuff.toString();
        }
}
