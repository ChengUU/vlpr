package com.sgc.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sgc.bean.TemplateType;
import com.sgc.dao.Dao;
import com.sgc.dao.TemplateTypeDaoProxy;
import com.sgc.dao.TemplateTypeFactory;
import com.sgc.util.CreatecdIcon;
import com.sgc.util.MyDocument;


public class BookTypeAddIFrame extends JInternalFrame {

	private JFormattedTextField typeNum;
	private JTextField templateTypeName;

	
	/**
	 * Create the frame
	 */
	public BookTypeAddIFrame() {
		super();
		setIconifiable(true);							// 设置窗体可最小化－－－必须
		setClosable(true);
		setTitle("模板类别添加");
		setBounds(100, 100, 500, 300);

		// 类别添加窗体内容面板
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(400, 80));
		getContentPane().add(panel, BorderLayout.NORTH);

		final JLabel label_4 = new JLabel();
		ImageIcon bookTypeAddIcon=CreatecdIcon.add("TmpTypeAdd.jpg");
		label_4.setIcon(bookTypeAddIcon);
		label_4.setPreferredSize(new Dimension(400, 80));
		label_4.setText("图书类别图片（400*80）");
		panel.add(label_4);

		final JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(100, 0));
		getContentPane().add(panel_2, BorderLayout.WEST);

		final JLabel label = new JLabel();
		panel_2.add(label);

		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new FlowLayout());
		getContentPane().add(panel_3, BorderLayout.CENTER);

		final JLabel label_1 = new JLabel();
		label_1.setPreferredSize(new Dimension(390, 50));
		panel_3.add(label_1);

		final JLabel label_2 = new JLabel();
		label_2.setPreferredSize(new Dimension(160, 20));
		label_2.setText("模板类别名称：");
		panel_3.add(label_2);

		templateTypeName = new JTextField();
		templateTypeName.setDocument(new MyDocument(2));
		templateTypeName.setColumns(10);
		panel_3.add(templateTypeName);

		final JLabel label_3 = new JLabel();
		label_3.setPreferredSize(new Dimension(160, 20));
		label_3.setText("模板类别编号：");
		panel_3.add(label_3);

		typeNum = new JFormattedTextField(NumberFormat.getIntegerInstance());
		typeNum.setColumns(2);
		typeNum.setValue(1);
		panel_3.add(typeNum);


		final JButton button = new JButton();
		button.setText("保存");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				if(templateTypeName.getText().length()==0){
					JOptionPane.showMessageDialog(null, "模板类别为必填项");
					return;
				}
				if(typeNum.getText().length()==0){
					JOptionPane.showMessageDialog(null, "模板类别编码为必填项");
					return;
				}
				// 构造模板类别对象
				int type=Integer.valueOf(typeNum.getText().trim());
				String typeName=templateTypeName.getText().trim();
				TemplateType templateType=new TemplateType(type,typeName);
				Dao<TemplateType> dao= TemplateTypeFactory.getDaoInstance();
				boolean i=dao.doInsert(templateType);
				if(i){
					JOptionPane.showMessageDialog(null, "添加成功！");
					doDefaultCloseAction();
				}
			}
		});
		panel_3.add(button);

		final JButton buttonDel = new JButton();
		buttonDel.setText("关闭");
		buttonDel.addActionListener(new ActionListener(){
			public void actionPerformed(final ActionEvent e) {
				doDefaultCloseAction();
			}
		});
		panel_3.add(buttonDel);
		setVisible(true);
	}
}
