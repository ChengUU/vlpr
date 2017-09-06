package com.sgc.iframe;

import com.sgc.test.ImagePanel;
import com.sgc.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by ChengXX on 2017/3/11.
 */
public class MainUI extends JInternalFrame implements ActionListener{
    //定义事件命令
    public static final String IMAGE_CMD="选择图像";
    public static final String PROCESS_CMD="处理";
    public static final String SAVE_CMD="保存";
    public static final String GRAY_HISTOGRAM_CMD="灰度直方图";
    public static final String OPEN_OP_CMD="开操作";
    public static final String CLOSE_OP_CMD="闭操作";

    // 带宽参数常量
    public final static Double[] HR_BANDWIDTH=new Double[]{4.0,6.0,8.0,10.0,12.0,14.0,16.0};
    public final static Double[] HT_BANDWIDTH=new Double[]{2.0,4.0,6.0,8.0,10.0};
    public final static Double[] HL_BANDWIDTH=new Double[]{0.425,0.85,1.275,1.7};

    private double[] bandwiths=new double[3];
    private int[] segs=new int[]{3,6,8};

    private JButton imgBtn;
    private JButton processBtn;
    private JButton saveBtn;
    private ImagePanel imagePanel;
    private JButton grayHisBtn;
    private JButton openOpBtn;
    private JButton closeOpBtn;
    private JComboBox<Double> rcombobox;
    private JComboBox<Double> tcombobox;
    private JComboBox<Double> lcombobox;

    //image
    private BufferedImage srcImage;
    public MainUI(){
        this.setTitle("系统流程演示");
        imgBtn=new JButton(IMAGE_CMD);
        processBtn=new JButton(PROCESS_CMD);
        saveBtn=new JButton(SAVE_CMD);
        grayHisBtn=new JButton(GRAY_HISTOGRAM_CMD);
        saveBtn.setEnabled(false);
        openOpBtn=new JButton(OPEN_OP_CMD);
        closeOpBtn=new JButton(CLOSE_OP_CMD);
        rcombobox=new JComboBox<>(HR_BANDWIDTH);
        rcombobox.setSelectedIndex(0);
        tcombobox=new JComboBox<>(HT_BANDWIDTH);
        rcombobox.setSelectedIndex(0);
        lcombobox=new JComboBox<>(HL_BANDWIDTH);
        rcombobox.setSelectedIndex(0);


        //buttons
        JPanel btnPanel=new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(rcombobox);
        btnPanel.add(tcombobox);
        btnPanel.add(lcombobox);
        btnPanel.add(imgBtn);
        btnPanel.add(processBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(grayHisBtn);
        btnPanel.add(openOpBtn);
        btnPanel.add(closeOpBtn);
        
        //filter
        imagePanel=new ImagePanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(imagePanel, BorderLayout.CENTER);
        getContentPane().add(btnPanel,BorderLayout.SOUTH);
        //setup listener
        setupActionListener();
        // show this frame
        openview();
    }


    public void actionPerformed(ActionEvent e){
        if(SwingUtilities.isEventDispatchThread()){
            System.out.println("Event Dispath Thread!!!");
        }
        if(null==srcImage){
            JOptionPane.showMessageDialog(this,"请选择图像源文件");
            return;
        }
        String actionCommand=e.getActionCommand();
        System.out.print("Command:"+actionCommand);
        if(PROCESS_CMD.equals(actionCommand)){
            imagePanel.setSourceImage(srcImage);
            bandwiths[0]=(double)rcombobox.getSelectedItem();
            bandwiths[1]=(double)tcombobox.getSelectedItem();
            bandwiths[2]=(double)lcombobox.getSelectedItem();
            imagePanel.process(bandwiths,segs);
            imagePanel.repaint();
            saveBtn.setEnabled(true);
        }else if(SAVE_CMD.equals(actionCommand)){
//            imagePanel.writeDestImg();
//            imagePanel.writeSrcImg();
            imagePanel.writeTemplateImg();
            saveBtn.setEnabled(false);
        }else if(GRAY_HISTOGRAM_CMD.equals(actionCommand)){
            // 定位测试
            //imagePanel.connRegTest();
//            imagePanel.layerGrayTest();
            // 精确处理测试
           // imagePanel.checkTest();
            imagePanel.rgColorSpcClusTest();
            saveBtn.setEnabled(true);
            imagePanel.repaint();
        }else if(OPEN_OP_CMD.equals(actionCommand)){
            imagePanel.openOp();
            imagePanel.repaint();
        }else if(CLOSE_OP_CMD.equals(actionCommand)){
            imagePanel.closeOp();
            imagePanel.repaint();
        }
    }

    private void setupActionListener(){
        imgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    JFileChooser chooser=new JFileChooser();
                   Util.setFileTypeFilter(chooser);
                    chooser.showOpenDialog(null);
                    File f=chooser.getSelectedFile();
                    if(null!=f){
                        srcImage= ImageIO.read(f);
                        imagePanel.setSourceImage(srcImage);
                        imagePanel.repaint();
                    }

                }catch(IOException ex){ex.printStackTrace();}
            }
        });
        processBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        grayHisBtn.addActionListener(this);
        openOpBtn.addActionListener(this);
        closeOpBtn.addActionListener(this);
    }


    private void openview(){
        setIconifiable(true);							// 设置窗体可最小化
        setClosable(true);								// 设置窗体可关闭
        setPreferredSize(new Dimension(1366,668));
        pack();
        setVisible(true);
    }
}
