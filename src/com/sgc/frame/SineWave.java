package com.sgc.frame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by ChengXX on 2017/3/9.
 */
class SineDraw extends JPanel{
    private static int SCALEFACTOR=200;
    private int cycles;
    private int points;
    private double[] sines;
    private int[] pts;
    public SineDraw(){setCycle(5);}
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int maxWidth = getWidth();
        double hstep = (double) maxWidth / (double) points;
        int maxHeight = getHeight();
        pts = new int[points];
        for (int i = 0; i < points; i++) {
            pts[i] = (int) (sines[i] * maxHeight / 2 * .95 + maxHeight/2);
        }
        g.setColor(Color.RED);
        for (int i = 1; i < points; i++) {
            int x1 = (int) ((i - 1) * hstep);
            int x2 = (int) (i * hstep);
            int y1 = pts[i - 1];
            int y2 = pts[i];
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public void setCycle(int cycles){
        this.cycles=cycles;
        points=SCALEFACTOR*cycles*2;
        sines=new double[points];
        for(int i=0;i<points;i++){
            double radians=(Math.PI/SCALEFACTOR)*i;
            sines[i]=Math.sin(radians);
        }
        repaint();
        System.out.println("repaint");
    }
}
public class SineWave extends JFrame{
    private SineDraw sines=new SineDraw();
    private JSlider adjustCycle=new JSlider(1,30,5);
    public SineWave(){
        add(sines);
        add(BorderLayout.SOUTH,adjustCycle);
        adjustCycle.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sines.setCycle(Integer.valueOf(((JSlider)e.getSource()).getValue()));
            }
        });
        setBounds(0,0,700,400);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        sines.setVisible(true);
    }
    public static void main(String[] args){
        new SineWave();
    }
}
