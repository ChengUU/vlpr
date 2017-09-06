package com.sgc.test;

import com.sgc.photo.TEST;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.L;

/**
 * Created by ChengXX on 2017/3/14.
 */
public class RGBTOLabTest {

    public static void main(String[] args){
        double R=167;
        double G=39;
        double B=42;
//        List<Double> rgb=new ArrayList<>();
//        rgb.add(R);
//        rgb.add(G);
//        rgb.add(B);
//        TEST.rgbToLab(rgb);

//        System.out.println(""+rgb.get(0)+" "+rgb.get(1)+" "+rgb.get(2));
        System.out.println(""+0.433953*(1<<20)+" "+ 0.376219*(1<<20)+" "+0.189828*(1<<20));
    }
}
