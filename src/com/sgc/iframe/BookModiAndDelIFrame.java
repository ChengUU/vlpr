package com.sgc.iframe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.sgc.JComPz.Item;
import com.sgc.JComPz.MapPz;
import com.sgc.bean.CharTemplate;
import com.sgc.dao.CharTemplateDaoProxy;
import com.sgc.bean.TemplateType;
import com.sgc.dao.TemplateTypeDaoProxy;
import com.sgc.main.LicensePR;
import com.sgc.util.CreatecdIcon;
import com.sgc.util.MyDocument;
import com.sgc.util.Util;

/**
 * 名称：图书修改窗体
 *
 */
public class BookModiAndDelIFrame extends JInternalFrame {
    private JTable table;
    private JTextField tmpId;
    private JTextField typeName;
    private JTextField tmpWidth;
    private JTextField tmpHeight;
    private JTextField fileName;
    private JTextField repchar;
    private JComboBox bookType;
    private JButton imgBtn;
    DefaultComboBoxModel bookTypeModel;
    private Item item;
    Map map = new HashMap();
    private String[] columnNames;
    private Map m = MapPz.getMap();

    private Object[][] getFileStates(List<CharTemplate> list) {//取数据库中图书相关信息放入表格中
        String[] columnNames = {"模板编号", "模板类别", "宽", "高", "模板文件", "代表字符"};
        Object[][] results = new Object[list.size()][columnNames.length];//二维数组用来保存所有记录

        for (int i = 0; i < list.size(); i++) {//遍历list
            CharTemplate bookinfo = list.get(i);//取出模板记录
            results[i][0] = bookinfo.getId();//设置模板编号
            String booktypename = String.valueOf(MapPz.getMap().get(bookinfo.getTypeId()));//获得模板类别名称
            results[i][1] = booktypename;//设置模板类别名称
            results[i][2] = bookinfo.getWidth();//设置模板宽
            results[i][3] = bookinfo.getHeight();    //设置模板高
            results[i][4] = bookinfo.getPath();//设置模板文件
            results[i][5] = bookinfo.getRepChar();//设置代表
        }
        return results;//范围二维数组的模板记录

    }

    public BookModiAndDelIFrame() {
        super();
        final BorderLayout borderLayout = new BorderLayout();//边框布局管理器
        getContentPane().setLayout(borderLayout);//使用边框布局管理器
        setIconifiable(true);// 设置窗体可最小化
        setClosable(true);// 设置窗体可关闭
        setTitle("模板信息修改");// 设置窗体标题
        setBounds(100, 100, 593, 406);// 设置窗体位置和大小


        final JPanel mainPanel = new JPanel();//主面板
        final BorderLayout borderLayout_1 = new BorderLayout();//边框布局管理器
        borderLayout_1.setVgap(5);//设置组件之间垂直距离
        mainPanel.setLayout(borderLayout_1);//使用边框布局管理器
        mainPanel.setBorder(new EmptyBorder(5, 10, 5, 10));//设置边框
        getContentPane().add(mainPanel);//将主面板添加到窗体中

        final JScrollPane scrollPane = new JScrollPane();//滚动面板
        mainPanel.add(scrollPane);//将滚动面板添加到主面板中

        CharTemplateDaoProxy dao = (CharTemplateDaoProxy) new CharTemplateDaoProxy();

        Object[][] results = getFileStates(dao.findAll());//模板记录
        columnNames = new String[]{"模板编号", "模板类别", "宽", "高", "模板文件", "代表字符"};//列名列表
        table = new JTable(results, columnNames);//创建表格
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);//自适应窗体
        //鼠标单击表格中的内容产生事件,将表格中的内容放入文本框中
        table.addMouseListener(new TableListener());
        scrollPane.setViewportView(table);//将表格添加到滚动面板中

        final JPanel bookPanel = new JPanel();//书籍修改面板
        mainPanel.add(bookPanel, BorderLayout.SOUTH);//添加到主面板底端
        final GridLayout gridLayout = new GridLayout(0, 6);//网格布局
        gridLayout.setVgap(5);//设置组件之间垂直距离
        gridLayout.setHgap(5);//设置组件之间平行距离
        bookPanel.setLayout(gridLayout);//设置书籍添加面板布局

        final JLabel tmpIdLabel = new JLabel();//创建图书编号标签
        tmpIdLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        tmpIdLabel.setText("模板编号：");//设置标签文本
        bookPanel.add(tmpIdLabel);//添加到书籍修改面板
        tmpId = new JTextField();//创建书号文本框
        tmpId.setFocusable(false);
        bookPanel.add(tmpId);//添加到书籍修改面板

        final JLabel tmpTypeypeLabel = new JLabel();//创建书籍类别标签
        tmpTypeypeLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        tmpTypeypeLabel.setText("类       别：");//设置标签文本
        bookPanel.add(tmpTypeypeLabel);//添加到书籍修改面板

        bookType = new JComboBox();//创建书籍类别下拉框
        bookTypeModel = (DefaultComboBoxModel) bookType.getModel();//设置类别模型
        TemplateTypeDaoProxy tmpTypeDao = new TemplateTypeDaoProxy();
        List<TemplateType> list = tmpTypeDao.findAll();//从数据库中取出图书类别
        for (int i = 0; i < list.size(); i++) {//遍历图书类别
            TemplateType booktype = list.get(i);//获得图书类别
            item = new Item();//实例化图书类别选项
            item.setId(booktype.getType());//设置图书类别编号
            item.setName(booktype.getTypeName());//设置图书类别名称
            bookTypeModel.addElement(item);//添加图书类别元素
            map.put(booktype.getTypeName(), item);//以键值对的形式保存
        }
        bookPanel.add(bookType);//添加到书籍修改面板

        final JLabel repCharLabel = new JLabel();//创建书名标签
        repCharLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        repCharLabel.setText("代表字符：");//设置标签文本
        bookPanel.add(repCharLabel);//添加到书籍修改面板
        repchar = new JTextField();//书名文本框
        repchar.setDocument(new MyDocument(2));
        bookPanel.add(repchar);//添加到书籍修改面板

        final JLabel fileNameLabel = new JLabel();//创建作者标签
        fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        fileNameLabel.setText("模板文件：");//设置标签文本
        bookPanel.add(fileNameLabel);//添加到书籍修改面板
        fileName = new JTextField();//作者文本框
        fileName.setFocusable(false);
        bookPanel.add(fileName);
        imgBtn = new JButton("选择文件");
        imgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser(LicensePR.class.getResource("/templates/").getPath());
                Util.setFileTypeFilter(chooser);
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();
                if (null != f) {
                    fileName.setText(f.getName());
                }
            }
        });
        bookPanel.add(imgBtn);

        final JLabel tmpWidthLabel = new JLabel();//创建出版社标签
        tmpWidthLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        tmpWidthLabel.setText("模板宽：");//设置标签文本
        bookPanel.add(tmpWidthLabel);//添加到书籍修改面板
        tmpWidth = new JTextField();//出版社文本框
        bookPanel.add(tmpWidth);//添加到书籍修改面板

        final JLabel tmpHeightLabel = new JLabel();//创建译者标签
        tmpHeightLabel.setHorizontalAlignment(SwingConstants.CENTER);//水平居中
        tmpHeightLabel.setText("模板高：");//设置标签文本
        bookPanel.add(tmpHeightLabel);//添加到书籍修改面板
        tmpHeight = new JTextField();//译者文本框
        bookPanel.add(tmpHeight);//添加到书籍修改面板


        final JPanel bottomPanel = new JPanel();//创建底部面板
        bottomPanel.setBorder(new LineBorder(
                SystemColor.activeCaptionBorder, 1, false));//设置边框
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);//添加到窗体底端
        final FlowLayout flowLayout = new FlowLayout();//流布局管理器
        flowLayout.setVgap(2);//设置组件之间垂直距离
        flowLayout.setHgap(30);//设置组件之间平行距离
        flowLayout.setAlignment(FlowLayout.RIGHT);//设置向右对齐
        bottomPanel.setLayout(flowLayout);//设置底部面板布局

        final JButton updateButton = new JButton();//创建修改按钮
        updateButton.addActionListener(new UpdateBookActionListener());//注册监听器
        updateButton.setText("修改");//设置按钮文本
        bottomPanel.add(updateButton);//添加到底部面板

        final JButton delButton = new JButton();//创建删除按钮
        delButton.addActionListener(new DelTmpActionListener());
        delButton.setText("删除");//设置按钮文本
        bottomPanel.add(delButton);//添加到底部面板

        final JButton closeButton = new JButton();//创建关闭按钮
        closeButton.addActionListener(new ActionListener() {//注册监听器
            public void actionPerformed(final ActionEvent e) {
                doDefaultCloseAction();//关闭窗体
            }
        });
        closeButton.setText("关闭");//设置按钮文本
        bottomPanel.add(closeButton);//添加到底部面板

        final JLabel headLogo = new JLabel();//图片标签
        ImageIcon bookModiAndDelIcon = CreatecdIcon.add("TmpModify.jpg");//图片图标
        headLogo.setIcon(bookModiAndDelIcon);//设置标签显示图片
        headLogo.setOpaque(true);//设置图片标签不透明
        headLogo.setBackground(Color.CYAN);//设置标签背景颜色
        headLogo.setPreferredSize(new Dimension(400, 80));//设置标签的大小
        headLogo.setBorder(new LineBorder(
                SystemColor.activeCaptionBorder, 1, false));//设置标签边框
        getContentPane().add(headLogo, BorderLayout.NORTH);//添加到窗体上端

        setVisible(true);//显示窗体可见
    }

    class TableListener extends MouseAdapter {
        public void mouseClicked(final MouseEvent e) {
            String tmpIds, typenames, tmpWidths, tmpHeights,
                    filenames, repchars;//声明变量
            int selRow = table.getSelectedRow();//获得所选行号
            tmpIds = table.getValueAt(selRow, 0).toString().trim();//获得书号
            typenames = table.getValueAt(selRow, 1).toString().trim();//获得类别编号
            tmpWidths = table.getValueAt(selRow, 2).toString().trim();//获得书名
            tmpHeights = table.getValueAt(selRow, 3).toString().trim();//获得作者
            filenames = table.getValueAt(selRow, 4).toString().trim();//获得译者
            repchars = table.getValueAt(selRow, 5).toString().trim();//获得出版社
            tmpId.setText(tmpIds);//设置书号文本框为获得的书号信息
            bookTypeModel.setSelectedItem(map.get(typenames));//设置类别下拉框所选项
            tmpWidth.setText(tmpWidths);//设置书名文本框为获得的书名信息
            tmpHeight.setText(tmpHeights);//设置作者文本框为获得的作者信息
            fileName.setText(filenames);//设置译者文本框为获得的译者信息
            repchar.setText(repchars);//设置出版社文本框为获得的出版社信息
        }
    }

    class UpdateBookActionListener implements ActionListener {
        public void actionPerformed(final ActionEvent e) {
            if (tmpWidth.getText().trim().length() == 0) {//判断是否输入了作者
                JOptionPane.showMessageDialog(null, "模板宽不可以为空");
                return;
            }
            if (tmpHeight.getText().trim().length() == 0) {//判断是否输入了出版社
                JOptionPane.showMessageDialog(null, "模板高不可以为空");
                return;
            }
            if (fileName.getText().trim().length() == 0) {//判断是否输入了出版日期
                JOptionPane.showMessageDialog(null, "模板文件不可以为空");
                return;
            }
            if (repchar.getText().trim().length() == 0) {//判断是否输入了书籍价格
                JOptionPane.showMessageDialog(null, "代表字符不可以为空");
                return;
            }
            int tmpIds = Integer.valueOf(tmpId.getText().trim());//获得书籍编号
            Object selectedItem = bookTypeModel.getSelectedItem();//书籍类别选项
            System.out.println(selectedItem);
            if (selectedItem == null) return;
            Item item = (Item) selectedItem;//获得所选类别
            int bookTypes = item.getId();//获得类别编号
            CharTemplate charTemplate = new CharTemplate();
            // 模板高度
            int ch = Integer.valueOf(tmpHeight.getText().trim());
            // 模板宽度
            int cw = Integer.valueOf(tmpWidth.getText().trim());
            // 模板文件
            String fname = fileName.getText();
            // 代表字符
            String repChar = repchar.getText().trim();
            charTemplate.setId(tmpIds);
            charTemplate.setTypeId(bookTypes);
            charTemplate.setWidth(cw);
            charTemplate.setHeight(ch);
            charTemplate.setPath(fname);
            charTemplate.setRepChar(repChar);

            CharTemplateDaoProxy dao = new CharTemplateDaoProxy();

            boolean i = dao.update(charTemplate);
            System.out.println(i);
            if (i) {//如果返回更新记录数为1，表示修改成功
                JOptionPane.showMessageDialog(null, "修改成功");
                Object[][] results = getFileStates(dao.findAll());//重新获得书籍信息
                DefaultTableModel model = new DefaultTableModel();//获得表格模型
                table.setModel(model);//设置表格模型
                model.setDataVector(results, columnNames);//设置模型数据和列名
            }

        }
    }
    class DelTmpActionListener implements ActionListener {
        public void actionPerformed(final ActionEvent e) {
            int tmpIds = Integer.valueOf(tmpId.getText().trim());//获得书籍编号
            if ("".equals(tmpIds)) {
                JOptionPane.showMessageDialog(null, "请选择将要删除的模板......");
                return;
            }
            CharTemplateDaoProxy dao = new CharTemplateDaoProxy();
            int n = JOptionPane.showConfirmDialog(null, "确定删除该模板吗?", "确认对话框", JOptionPane.YES_NO_OPTION);
            boolean i = false;
            if (n == JOptionPane.YES_OPTION) i = dao.delete(tmpIds);
            else if (n == JOptionPane.NO_OPTION) return;
            if (i) {//如果返回更新记录数为1，表示修改成功
                JOptionPane.showMessageDialog(null, "删除成功");
                Object[][] results = getFileStates(dao.findAll());//重新获得书籍信息
                DefaultTableModel model = new DefaultTableModel();//获得表格模型
                table.setModel(model);//设置表格模型
                model.setDataVector(results, columnNames);//设置模型数据和列名
            }

        }
    }
}

