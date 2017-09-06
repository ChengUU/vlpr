package com.sgc.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


import com.sgc.JComPz.Item;
import com.sgc.bean.CharTemplate;
import com.sgc.dao.*;
import com.sgc.bean.TemplateType;
import com.sgc.main.LicensePR;
import com.sgc.util.CreatecdIcon;
import com.sgc.util.MyDocument;
import com.sgc.util.Util;

/**
 * 名称：图书添加窗体
 * 
 */
public class BookAddIFrame extends JInternalFrame {
	private JTextField repchar;
	private String path;
	private JTextField width;
	private JTextField height;
	private JComboBox templateType;
	private JButton buttonadd;
	private JButton buttonclose;
	private JButton imgBtn;
	DefaultComboBoxModel bookTypeModel;

	Map map=new HashMap();
	public BookAddIFrame() {
		super();
		final BorderLayout borderLayout = new BorderLayout();//创建边框布局管理器
		getContentPane().setLayout(borderLayout);			//设置布局
		setIconifiable(true);							// 设置窗体可最小化
		setClosable(true);								// 设置窗体可关闭
		setTitle("识别模板添加");						// 设置窗体标题
		setBounds(100, 100, 396, 260);					// 设置窗体位置和大小

		final JPanel mainPanel = new JPanel();			//创建中心面板
		mainPanel.setBorder(new EmptyBorder(5, 10, 5, 10));//设置边框
		final GridLayout gridLayout = new GridLayout(0, 4);//创建表格布局管理器
		gridLayout.setVgap(5);					//设置组件之间垂直距离
		gridLayout.setHgap(5);					//设置组件之间平行距离
		mainPanel.setLayout(gridLayout);		//设置布局
		getContentPane().add(mainPanel);		//将中心面板加入到窗体

		final JLabel bookTypeLabel = new JLabel();//创建书籍类别标签
		bookTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);//设置平行对齐方式
		bookTypeLabel.setText("类别：");//设置标签文本
		mainPanel.add(bookTypeLabel);//添加到中心面板

		templateType = new JComboBox();//创建书籍类别下拉框
		bookTypeModel= (DefaultComboBoxModel)templateType.getModel();//设置类别模型

        // 数据库访问
       TemplateTypeDaoProxy dao= (TemplateTypeDaoProxy)TemplateTypeFactory.getDaoInstance();

		List<TemplateType> list=dao.findAll();//从数据库中取出图书类别
		for(int i=0;i<list.size();i++){		//遍历图书类别
			TemplateType booktype=list.get(i);//获得图书类别
			Item item=new Item();//实例化图书类别选项
			item.setId(booktype.getType());//设置图书类别编号
			item.setName(booktype.getTypeName());//设置图书类别名称
			bookTypeModel.addElement(item);//添加图书类别元素
		}
		mainPanel.add(templateType);//添加到中心面板



		final JLabel writerLabel = new JLabel();//创建模板宽度标签
		writerLabel.setHorizontalAlignment(SwingConstants.CENTER);//设置平行对齐方式
		writerLabel.setText("模板宽：");//设置标签文本
		mainPanel.add(writerLabel);//添加到中心面板

		width = new JFormattedTextField(NumberFormat.getIntegerInstance());;//创建模板宽度文本框
		width.setDocument(new MyDocument(3));//设置作者文本框最大输入值为10
		mainPanel.add(width);//添加到中心面板

		final JLabel publisherLabel = new JLabel();//创建模板高度标签
		publisherLabel.setText("模板高：");//设置标签文本
		mainPanel.add(publisherLabel);//添加到中心面板

		height = new JFormattedTextField(NumberFormat.getIntegerInstance());//创建模板高度文本框
        width.setDocument(new MyDocument(3));//设置作者文本框最大输入值为10
		mainPanel.add(height);//添加到中心面板

		final JLabel translatorLabel = new JLabel();//创模板文件路径标签
		translatorLabel.setHorizontalAlignment(SwingConstants.CENTER);//设置平行对齐方式
		translatorLabel.setText("模板文件：");//设置标签文本
		mainPanel.add(translatorLabel);//添加到中心面板

		imgBtn = new JButton("选择模板");//创建译者文本框
        imgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    JFileChooser chooser=new JFileChooser(LicensePR.class.getResource("/templates/").getPath());
                    Util.setFileTypeFilter(chooser);
                    chooser.showOpenDialog(null);
                    File f=chooser.getSelectedFile();
                    if(null!=f){
                        path=f.getName();
                    }
            }
        });
		mainPanel.add(imgBtn);//添加到中心面板



		final JLabel priceLabel = new JLabel();//创建代表字符标签
		priceLabel.setHorizontalAlignment(SwingConstants.CENTER);//设置平行对齐方式
		priceLabel.setText("代表字符：");//设置标签文本
		mainPanel.add(priceLabel);//添加到中心面板

		repchar=   new   JTextField();//创建价格文本框
		repchar.setDocument(new MyDocument(2));//设置价格文本框最大输入值为5
		mainPanel.add(repchar);//添加到中心面板

		final JPanel bottomPanel = new JPanel();//创建底部面板
		bottomPanel.setBorder(new LineBorder(SystemColor.
							activeCaptionBorder, 1, false));//设置边框
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);//添加到窗体中
		final FlowLayout flowLayout = new FlowLayout();//流布局管理器
		flowLayout.setVgap(2);	//设置组件之间垂直距离
		flowLayout.setHgap(30);//设置组件之间平行距离
		flowLayout.setAlignment(FlowLayout.RIGHT);//设置对齐方式
		bottomPanel.setLayout(flowLayout);//设置底部面板布局

		buttonadd= new JButton();//创建添加按钮
		buttonadd.addActionListener(new AddBookActionListener());//注册监听器
		buttonadd.setText("添加");//设置按钮文本
		bottomPanel.add(buttonadd);//添加到底部面板

		buttonclose = new JButton();//创建关闭按钮
		buttonclose.addActionListener(new CloseActionListener());//注册监听器
		buttonclose.setText("关闭");//设置按钮文本
		bottomPanel.add(buttonclose);//添加到底部面板

		final JLabel imageLabel = new JLabel();//图片标签
		ImageIcon bookAddIcon=CreatecdIcon.add("TmpAdd.jpg");//图片图标
		imageLabel.setIcon(bookAddIcon);//设置标签显示图片
		imageLabel.setPreferredSize(new Dimension(400, 80));//设置标签的大小
		imageLabel.setBorder(new LineBorder(SystemColor.
						activeCaptionBorder, 1, false));//设置边框
		getContentPane().add(imageLabel, BorderLayout.NORTH);//添加到窗体中
		imageLabel.setText("图书信息添加(LOGO图片)");//设置标签文本

		setVisible(true);											// 显示窗体可见
	}
	class ISBNFocusListener extends FocusAdapter {
		public void focusLost(FocusEvent e){
		    CharTemplateDaoProxy dao=(CharTemplateDaoProxy) CharTemplateDaoFactory.getDaoInstance();
		    List list=dao.queryByPath(path);
			if(null!=list&&!list.isEmpty()){
				JOptionPane.showMessageDialog(null, "模板重复......");
				return;
			}
		}
	}
	class ISBNkeyListener extends KeyAdapter {
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == 13){
				buttonadd.doClick();
			}

		}
	}
	class CloseActionListener implements ActionListener {			// 添加关闭按钮的事件监听器
		public void actionPerformed(final ActionEvent e) {
			doDefaultCloseAction();
		}
	}
	class AddBookActionListener implements ActionListener {		// 添加按钮的单击事件监听器
		public void actionPerformed(final ActionEvent e) {
			if(repchar.getText().trim().length()==0){//判断是否输入了书籍名称
				JOptionPane.showMessageDialog(null, "代表字符不可以为空");
				return;
			}
			if(width.getText().trim().length()==0){//判断是否输入了作者
				JOptionPane.showMessageDialog(null, "模板宽度不可以为空");
				return;
			}
			if(height.getText().trim().length()==0){//判断是否输入了出版日期
				JOptionPane.showMessageDialog(null, "模板高度不可以为空");
				return;
			}
			if(null!=path&&path.trim().length()==0){//判断是否输入了书籍价格
				JOptionPane.showMessageDialog(null, "模板文件不可以为空");
				return;
			}
			Object selectedItem = templateType.getSelectedItem();//书籍类别选项
			if (selectedItem == null) return;
			Item item = (Item) selectedItem;	//获得所选类别
			int tmptTypes=item.getId();		//获得类别编号
			int cw=Integer.valueOf(width.getText().trim());//获得模板宽度
			int ch= Integer.valueOf(height.getText().trim());//获得模板高度
			String reprentChar= repchar.getText().trim();//获得代表字符
            CharTemplate charTemplate=new CharTemplate();
            charTemplate.setTypeId(tmptTypes);
            charTemplate.setWidth(cw);
            charTemplate.setHeight(ch);
            charTemplate.setRepChar(reprentChar);
            charTemplate.setPath(path);
            CharTemplateDaoProxy dao=new CharTemplateDaoProxy();
			boolean i=dao.doInsert(charTemplate);
			if(i){	//如果返回更新记录数为1，表示添加成功
				JOptionPane.showMessageDialog(null, "添加成功");
				doDefaultCloseAction();
			}
		}
	}

}
