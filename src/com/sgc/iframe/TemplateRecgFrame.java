package com.sgc.iframe;

import com.sgc.geometric.ConnRegion;
import com.sgc.photo.*;
import com.sgc.res.ExcisionUnit;
import com.sgc.res.RadonRes;
import com.sgc.service.*;
import com.sgc.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by ChengXX on 2017/4/21.
 */
public class TemplateRecgFrame extends JInternalFrame {

    private BufferedImage sourceImage;                      // 图像源文件
    private BufferedImage[] procImages=new BufferedImage[2];// 处理流程文件
    List<ExcisionUnit> excisions;                            // 字符分割结果
    private String recgResStr;                              // 识别结果字符串
    private JPanel srcImgShowPanel;                         // 图像源文件显示域
    JPanel btnsPanel;                                       // 按钮区域
    JPanel resShowPanel;                                    // 结果显示域

    private JPanel locRes;                  // 定位结果
    private JPanel skewCorrectRes;          // 倾斜校正结果
    private  JPanel charExcRes;             // 字符分割结果
    private JTextField recgRes;             // 车牌识别结果

    private JButton fileChooseBtn;          // 文件选择按钮
    private JButton objLocBtn;              // 车牌定位按钮
    private JButton skewCorrectBtn;         // 倾斜校正按钮
    private JButton charExcBtn;             // 字符切割按钮
    private JButton recgBtn;                //识别按钮
    private JButton closeBtn;  // 退出按钮

    public TemplateRecgFrame(){
        setIconifiable(true);							    // 设置窗体可最小化
        setClosable(true);
        setLocation(150,0);// 设置窗体可关闭
        setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT)); //设置窗体大小
        setTitle("车牌识别-模板匹配识别");

        JPanel mainPanel=new JPanel();
        Dimension rect=new Dimension(512,FRAME_HEIGHT);
        mainPanel.setPreferredSize(rect);
        getContentPane().add(mainPanel,BorderLayout.CENTER);

        // 初始化图像源文件显示区域
        srcImgShowPanel=new JPanel();
        rect=new Dimension(512,460);
        srcImgShowPanel.setPreferredSize(rect);
        srcImgShowPanel.setBackground(new Color(142,255,232));
        mainPanel.add(srcImgShowPanel,BorderLayout.CENTER);

        // 初始化按钮区域
        btnsPanel=new JPanel();
        rect=new Dimension(512,100);
        btnsPanel.setPreferredSize(rect);
        btnsPanel.setBackground(Color.YELLOW);
        mainPanel.add(btnsPanel,BorderLayout.SOUTH);
        btnsPanel.setLayout(new GridLayout(2,3));
        fileChooseBtn=new JButton("选择图像文件");
        fileChooseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser chooser = new JFileChooser();
                    Util.setFileTypeFilter(chooser);
                    chooser.showOpenDialog(null);
                    File f = chooser.getSelectedFile();
                    if (null != f) {
                        sourceImage = ImageIO.read(f);
                        paintSrcImg();
                        objLocBtn.setEnabled(true);// 给予下一步权限
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        objLocBtn=new JButton("车牌定位");
        objLocBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procImages[0]=locObj(sourceImage);
                paintLocObj();
                objLocBtn.setEnabled(false); //将当前按钮置为假
                skewCorrectBtn.setEnabled(true);// 给予下一步权限
            }
        });
        objLocBtn.setEnabled(false);
        skewCorrectBtn=new JButton("倾斜校正");
        skewCorrectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procImages[1]=skewCorrect(procImages[0]);
                paintSkewCorrectRes();
                skewCorrectBtn.setEnabled(false);//将当前按钮置为假
                charExcBtn.setEnabled(true);// 给予下一步权限
            }
        });
        skewCorrectBtn.setEnabled(false);
        charExcBtn=new JButton("字符分割");
        charExcBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excisions=charExc(procImages[1]);
                paintCharExcRes();
                charExcBtn.setEnabled(false);       //将当前按钮置为假
                recgBtn.setEnabled(true);           // 给予下一步权限
            }
        });
        charExcBtn.setEnabled(false);
        recgBtn=new JButton("字符识别");
        recgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 保存识别结果
                recgResStr=recg(excisions);
                recgRes.setText(recgResStr);
                // 显示识别结果
                recgBtn.setEnabled(false);
            }
        });
        recgBtn.setEnabled(false);
        closeBtn=new JButton("返回");
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                doDefaultCloseAction();
            }
        });

        btnsPanel.add(fileChooseBtn);
        btnsPanel.add(objLocBtn);
        btnsPanel.add(skewCorrectBtn);
        btnsPanel.add(charExcBtn);
        btnsPanel.add(recgBtn);
        btnsPanel.add(closeBtn);


        // 初始化处理结果
        resShowPanel=new JPanel();
        rect=new Dimension(288,FRAME_HEIGHT);
        resShowPanel.setPreferredSize(rect);
        resShowPanel.setBackground(new Color(0,106,0));

        JLabel label_1=new JLabel("定位结果");// 定位结果标签
        label_1.setFont(new Font("宋体",Font.BOLD,16));
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        resShowPanel.add(label_1);
        locRes=new JPanel();// 定位结果
        rect=new Dimension(288,130);
        locRes.setPreferredSize(rect);
        resShowPanel.add(locRes);

        JLabel label_2=new JLabel("倾斜校正结果");// 倾斜校正结果标签
        label_2.setFont(new Font("宋体",Font.BOLD,16));
        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        resShowPanel.add(label_2);
        skewCorrectRes=new JPanel();// 倾斜校正结果
        rect=new Dimension(288,130);
        skewCorrectRes.setPreferredSize(rect);
        resShowPanel.add(skewCorrectRes);

        JLabel label_3=new JLabel("字符分割结果");// 字符分割结果标签
        label_3.setFont(new Font("宋体",Font.BOLD,16));
        label_3.setHorizontalAlignment(SwingConstants.CENTER);
        resShowPanel.add(label_3);
        charExcRes=new JPanel();// 字符分割结果
        rect=new Dimension(288,130);
        charExcRes.setPreferredSize(rect);
        resShowPanel.add(charExcRes);

        JLabel label_4=new JLabel("识别结果");// 字符分割结果标签
        label_4.setFont(new Font("宋体",Font.BOLD,16));
        label_4.setHorizontalAlignment(SwingConstants.CENTER);
        resShowPanel.add(label_4);
        recgRes=new JTextField();// 字符分割结果
        recgRes.setFont(new Font("宋体",Font.BOLD,24));
        recgRes.setForeground(Color.RED);
        rect=new Dimension(288,40);
        recgRes.setPreferredSize(rect);
        resShowPanel.add(recgRes);


        getContentPane().add(resShowPanel,BorderLayout.EAST);

        pack();
        setVisible(true);// 设置窗体可见
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paintSrcImg();
        paintLocObj();
        paintSkewCorrectRes();
        paintCharExcRes();
    }

    /**
     * 进行字符识别
     * @param excisions
     * @return
     */

    private String recg(List<ExcisionUnit> excisions){
        int width,height;
        int len=excisions.size();
        BufferedImage[] images=new BufferedImage[len];
        int i=0;
        if(null!=excisions){
            Iterator<ExcisionUnit> ite=excisions.iterator();
            while (ite.hasNext()){
                ExcisionUnit excisionUnit=ite.next();
                if(excisionUnit==null) continue;
                width=excisionUnit.getWidth();
                height=excisionUnit.getHeight();
                BufferedImage temp=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                AbstractBufferedImageOp.setRGB(temp,0,0,width,height,excisionUnit.getPixels());
                images[i++]=temp;
            }
        }
        StringBuffer strb=new StringBuffer();
        // 汉字识别器
        long start=System.currentTimeMillis();
        Discriminator chnDiscriminator=CHNCharDiscriminator.getInstance();
        String first=chnDiscriminator.recg(images[0]);
        first=null!=first?first:"-";
        strb.append(first);
        // 字母识别器
        Discriminator alphaDiscriminator=AlphaCharDiscriminator.getInstance();
        String second=alphaDiscriminator.recg(images[1]);//第二位字母
        second=null!=second?second:"-";
        strb.append(second);
        // 数字字母识别器
        Discriminator alphaAndNumDiscriminator=AlphaAndNumCharDiscriminator.getInstance();
        for(int j=2;j<len;j++){
            String ch=alphaAndNumDiscriminator.recg(images[j]);
            ch=null!=ch?ch:"-";
            strb.append(ch);
        }
        long end=System.currentTimeMillis();
//        System.out.println("字符识别耗时="+(end-start)/1000.0);
        return strb.toString();
    }

    /**
     * 车牌字符分割
     * @param image
     * @return
     */

    public List<ExcisionUnit> charExc(BufferedImage image){
        ZoomFilter zoomFilter=new ZoomFilter();
        zoomFilter.setNewHeight(90);
        zoomFilter.setNewWidth(440);
       BufferedImage middleSection=zoomFilter.zoom(image);
        BorderDetFilter borderDetFilter=new BorderDetFilter();
        // 高通滤波除噪
        middleSection=borderDetFilter.dealSPCCharacter(middleSection);
        PhotoExcision photoExcision=new PhotoExcision();
        List<ExcisionUnit> excisions=photoExcision.doExcision(middleSection);
        excisions=NormalizationFactory.normalize(excisions);
        return excisions;
    }

    /**
     * 车牌倾斜校正
     * @param image
     * @return
     */
    private BufferedImage skewCorrect(BufferedImage image){
        // 彩色图像灰度化
        com.sgc.photo.GrayFilter grayFilter=new com.sgc.photo.GrayFilter();
        BufferedImage destImage=grayFilter.filter(image,null);

        //测试-图像转置
        TransposeFilter transposeFilter=new TransposeFilter();
        destImage=transposeFilter.filter(destImage,null);
        // 二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        destImage=binaryFilter.filter(destImage,null);
        // 测试 多结构元素边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        destImage=mseEdgeDet.filter(destImage,null);
        //倾斜校正
        // radon直线检测(图像倾斜)-水平方向检测
        RadonFilter radonFilter=new RadonFilter();
        RadonRes radonRes=radonFilter.radon(destImage,-45,45,1);
        RotationFilter rotationFilter=new RotationFilter();
        double dipAngle=radonRes.getDipAngle();
        dipAngle=45==dipAngle?0:dipAngle;
        dipAngle=-45==dipAngle?0:dipAngle;

        //垂直方向检测
        /*第一步:灰度化*/
        BufferedImage middleSection=grayFilter.filter(image,null);
        /*第二步:二值化*/
        middleSection=binaryFilter.filter(middleSection,null);
        /*第三步:边缘检测*/
        middleSection=mseEdgeDet.filter(middleSection,null);
        /*第四步:倾斜角度检测*/
        radonRes=radonFilter.radon(middleSection,-10,10,1);
        double verAngle=radonRes.getDipAngle();
        verAngle=10==verAngle?0:verAngle;
        verAngle=-10==verAngle?0:verAngle;
//        System.out.println("水平倾斜:"+dipAngle+"垂直倾斜:"+verAngle);
        destImage=rotationFilter.shearTransform(image,-dipAngle,-verAngle);

        middleSection=grayFilter.filter(destImage,null);
        BorderDetFilter borderDetFilter=new BorderDetFilter();
        ZoomFilter zoomFilter=new ZoomFilter();
        middleSection=zoomFilter.zoom(middleSection,440);
        middleSection=borderDetFilter.dealVerBlank(middleSection,false);
        binaryFilter.setThresholdType(BinaryFilter.SHIFT_THRESHOLD);
        middleSection=binaryFilter.filter(middleSection,null);
        middleSection=borderDetFilter.dealHorBorder(middleSection);
        return middleSection;
    }

    /**
     * 车牌定位
     * @param image
     * @return
     */
    private BufferedImage locObj(BufferedImage image){
        if(null==image){
            JOptionPane.showMessageDialog(this,"请选择图像源文件");
            return null;
        }
        long start=System.currentTimeMillis();
        // 彩色图像直方图均衡化
        HistogramEFilter histogramEFilter = new HistogramEFilter();
        BufferedImage destImage = histogramEFilter.filter(image, null);
        // 高斯模糊处理——去除图像细节,保留图像主要特征
        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter();
        destImage = gaussianBlurFilter.filter(destImage, null);
        // 分级灰度化
        ColorPicker[] colorPickers = new ColorPicker[4];
        colorPickers[0] = new BlueColorPicker(190f, 245f, 0.35f, 1f, 0.3f, 1f);
        colorPickers[1] = new YellowColorPicker(25f, 55f, 0.35f, 1f, 0.3f, 1f);
        colorPickers[2] = new WhiteColorPicker(0, 0, 0, 0.1f, 0.91f, 1f);
        colorPickers[3] = new BlackColorPicker(0, 0, 0, 0, 0, 0.35f);
        LayerGrayFilter layerGrayFilter = new LayerGrayFilter(colorPickers);
        destImage = layerGrayFilter.filter(destImage, null);
        // 分层二值化
        BufferedImage[] images = layerGrayFilter.binaryImage();
        // 灰度化
        com.sgc.photo.GrayFilter grayFilter = new com.sgc.photo.GrayFilter();
        BufferedImage middleSection = grayFilter.filter(image, null);
        int[][] se=new int[][] {{0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                {7,8,9,10,11,12,12,12,11,10,9,8,7},
                {0,7,8,9,10,11,12,11,10,9,8,7,0},
                {0,0,7,8,9,10,11,10,9,8,7,0,0},
                {0,0,0,7,8,9,10,9,8,7,0,0,0},
                {0,0,0,0,7,8,9,8,7,0,0,0,0},
                {0,0,0,0,0,7,7,7,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0}};
        // 高帽变换
        HatFilter hatFilter=new HatFilter();
        middleSection=hatFilter.onTopHat(middleSection,se);
        // E算子垂直边缘检测
        EOperatorEdgeDet eOperatorEdgeDet = new EOperatorEdgeDet();
        middleSection = eOperatorEdgeDet.filter(middleSection, null);
        // 二值化
        BinaryFilter binaryFilter = new BinaryFilter(BinaryFilter.SHIFT_THRESHOLD);
        middleSection = binaryFilter.filter(middleSection, null);

        // 连通区域检测
        ConnRegDet connRegDet = new ConnRegDet();
        java.util.List<ConnRegion> res = connRegDet.findConnRegs(images, middleSection);
//        System.out.println("1.连通区域数:" + res.size());

        FacticityValidator facticityValidator=new FacticityValidator();
        // 重新获取遍历器
        res=facticityValidator.validate(image,res);
//        System.out.println("2.候选区域数量:"+res.size());
        int size=res.size();
        if(size<1) {
            res=kwfcmLocObj(image);
//            System.out.println(res);
//            System.out.println("2.1候选区域数量:"+res.size());
            res=facticityValidator.validate(image,res);

        }
        // 第二层检查结果
//        System.out.println("3.候选区域数量:"+res.size());

        destImage= connRegDet.show(image,res);
        destImage=binaryFilter.filter(destImage,null);
        ObjRegDet objRegDet=new ObjRegDet();
        ConnRegion connRegion = res.get(0);
        middleSection= objRegDet.getObjReg(image,connRegion);
        long end=System.currentTimeMillis();
//        System.out.println("车牌定位耗时="+(end-start)/1000.0);
        return  middleSection;
    }
    private List<ConnRegion> kwfcmLocObj(BufferedImage image){
        PictureEnhanced pictureEnhanced=new PictureEnhanced();
        BufferedImage destImage=pictureEnhanced.enhance(image);
        BufferedImage middleSection=destImage;
        //边缘检测
        MSEEdgeDet mseEdgeDet=new MSEEdgeDet();
        middleSection=mseEdgeDet.prewittEdge(destImage);
        destImage=middleSection;
        //粗步去噪
        LicensePlateDenoise licensePlateDenoise=new LicensePlateDenoise();
        middleSection=licensePlateDenoise.preDenoise(middleSection);
        // 精确去噪
        middleSection=licensePlateDenoise.denoise(middleSection);
        //灰度能量聚类,去除为车牌边缘图
        GrayEnergyCluster grayEnergyCluster=new GrayEnergyCluster(7,11);
        middleSection=grayEnergyCluster.cluster(middleSection,destImage);
        // 图像二值化
        BinaryFilter binaryFilter=new BinaryFilter();
        binaryFilter.setThresholdType(BinaryFilter.OSTU_THRESHOLD);
        destImage=binaryFilter.filter(middleSection,null);
        middleSection=destImage;
        //水平聚类线性探测法
        ConnRegDet connRegDet=new ConnRegDet();
        middleSection=connRegDet.horizontalClusterSearch(destImage,24,0.60);
        //形态学操作
        int[][] C=new int[][]{ {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1}};
        // 开运算
        CloseFilter closeFilter=new CloseFilter();
        closeFilter.setStructureElements(C);
        middleSection=closeFilter.filter(middleSection,null);
        destImage=middleSection;
        middleSection=licensePlateDenoise.denoiseConn(middleSection);
        List<ConnRegion> list=connRegDet.findConnRegs(middleSection);
        return list;
    }

    public void paintSrcImg(){
        Graphics2D g2d=(Graphics2D)srcImgShowPanel.getGraphics();
        if(null==sourceImage) return;
        int width=512;
        int height=400;
        Image buffImage=createImage(width,height);
        Graphics2D g=(Graphics2D)buffImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        ZoomFilter zoomFilter=new ZoomFilter(width,height);
        BufferedImage image=zoomFilter.zoom(sourceImage);
        g.drawImage(image,0,0,512,400,this);
        g2d.drawImage(buffImage,0,0,this);
    }

    public void paintLocObj(){
        Graphics2D g2d=(Graphics2D)locRes.getGraphics();
        int width=locRes.getWidth();
        int height=locRes.getHeight();
        Image buffImage=createImage(width,height);
        Graphics2D g=(Graphics2D)buffImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, buffImage.getWidth(null), buffImage.getHeight(null));
        if(null==procImages[0]) return;
        ZoomFilter zoomFilter=new ZoomFilter(288,130);
        BufferedImage image=zoomFilter.zoom(procImages[0]);
        g.drawImage(image,0,0,288,130,this);
        g2d.drawImage(buffImage,0,0,this);
    }

    public void paintSkewCorrectRes(){
        Graphics2D g2d=(Graphics2D)skewCorrectRes.getGraphics();
        if(null==procImages[1]) return;
        ZoomFilter zoomFilter=new ZoomFilter(288,130);
        BufferedImage image=zoomFilter.zoom(procImages[1]);
        g2d.drawImage(image,0,0,288,130,this);
    }

    public void paintCharExcRes(){
        Graphics2D g2d=(Graphics2D)charExcRes.getGraphics();
        if(null==excisions) return;
        int width,height;
        ZoomFilter zoomFilter=new ZoomFilter();
        Iterator<ExcisionUnit> ite=excisions.iterator();
        int i=0;
        while (ite.hasNext()){
            ExcisionUnit excisionUnit=ite.next();
            if(excisionUnit==null) continue;
            width=excisionUnit.getWidth();
            height=excisionUnit.getHeight();
            BufferedImage temp=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            AbstractBufferedImageOp.setRGB(temp,0,0,width,height,excisionUnit.getPixels());
            zoomFilter.zoom(temp,35);
            g2d.drawImage(temp,i*40,0,35,70,this);
            i++;
        }

    }

    public static final int FRAME_WIDTH=800;
    public static final int FRAME_HEIGHT=600;
}
