package com.sgc.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.sgc.bean.TemplateType;
import com.sgc.dao.Dao;
import com.sgc.dao.TemplateTypeDaoProxy;
import com.sgc.dao.TemplateTypeFactory;
import com.sgc.util.CreatecdIcon;

public class BookTypeModiAndDelIFrame extends JInternalFrame {

	private JComboBox comboBox;
	private JTextField type,typeName;
	private JTextField BookTypeId;
	private JTable table;
	private String[] columnNames={ "序号","模板类别编号", "模板类别名称"};
	DefaultTableModel model;

	

	private Object[][] getFileStates(List<TemplateType> list){
		Object[][]results=new Object[list.size()][columnNames.length];
		for(int i=0;i<list.size();i++){
			TemplateType booktype=list.get(i);
			results[i][0]=booktype.getId();
			results[i][1]=booktype.getType();
			results[i][2]=booktype.getTypeName();
		}
		return results;
	         		
	}
	/**
	 * Create the frame
	 */
	public BookTypeModiAndDelIFrame() {
		super();
		setTitle("模板类别修改");
		setBounds(100, 100, 500, 350);
		setIconifiable(true);
		setClosable(true);
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.NORTH);

		final JLabel logoLabel = new JLabel();
		
		ImageIcon bookTypeModiAndDelIcon=CreatecdIcon.add("TmpTypeModify.jpg");
		logoLabel.setIcon(bookTypeModiAndDelIcon);
		
		logoLabel.setPreferredSize(new Dimension(400, 80));
		logoLabel.setText("logo");
		panel.add(logoLabel);

		final JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(400, 130));
		panel_1.add(scrollPane);

		Dao<TemplateType> dao= TemplateTypeFactory.getDaoInstance();

		model=new DefaultTableModel();
		try{
			Object[][] results=getFileStates(dao.findAll());
			model.setDataVector(results,columnNames);
			table = new JTable();
			table.setModel(model);
			table.addMouseListener(new TableListener());
			scrollPane.setViewportView(table);
		}catch (Exception e){e.printStackTrace();}

		

		final JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		final GridLayout gridLayout = new GridLayout(0, 6);
		gridLayout.setVgap(5);
		gridLayout.setHgap(5);
		panel_2.setLayout(gridLayout);
		panel_2.setPreferredSize(new Dimension(400, 43));

		final JLabel label = new JLabel();
		label.setText("序号：");
		panel_2.add(label);

		BookTypeId = new JTextField();
		BookTypeId.setFocusable(false);
		panel_2.add(BookTypeId);



		final JLabel label_1 = new JLabel();
		label_1.setText("类别编号：");
		panel_2.add(label_1);
		type=new JTextField();
		panel_2.add(type);


		final JLabel label_2 = new JLabel();
		label_2.setText("类别名称：");
		panel_2.add(label_2);
		typeName=new JTextField();
		panel_2.add(typeName);


		final JPanel panel_4 = new JPanel();
		panel_1.add(panel_4);

		final JButton buttonMod = new JButton();
		buttonMod.setText("修改");
		buttonMod.addActionListener(new ButtonAddListener());
		panel_4.add(buttonMod);

		final JButton buttonDel = new JButton();
		buttonDel.setText("删除");
		buttonDel.addActionListener(new ButtonDelListener());
		panel_4.add(buttonDel);

		final JButton buttonExit = new JButton();
		buttonExit.setText("退出");
		buttonExit.addActionListener(new CloseActionListener());
		panel_4.add(buttonExit);
		setVisible(true);

	}
	class TableListener extends MouseAdapter {
		public void mouseClicked(final MouseEvent e) {
			
			int selRow = table.getSelectedRow();
			BookTypeId.setText(table.getValueAt(selRow, 0).toString().trim());
			type.setText(table.getValueAt(selRow,1).toString().trim());
			typeName.setText(table.getValueAt(selRow,2).toString().trim());
			
		}
	}
	class ButtonAddListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int id=Integer.valueOf(BookTypeId.getText().trim());
			int typeNum=Integer.valueOf(type.getText().trim());
			String name=typeName.getText().trim();
			Dao<TemplateType> dao=TemplateTypeFactory.getDaoInstance();
			TemplateType templateType=new TemplateType(id,typeNum,name);
			boolean i=dao.update(templateType);
			System.out.println(i);
			if(i){
				JOptionPane.showMessageDialog(null, "修改成功");
				try{
				    Object[][] results=getFileStates(dao.findAll());
                    table.setModel(model);
					model.setDataVector(results,columnNames);
				}catch(Exception ex){ex.printStackTrace();}

			}
		}
	}
	class ButtonDelListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
		    String tmpTypeId=BookTypeId.getText().trim();
			if("".equals(tmpTypeId)){
				JOptionPane.showMessageDialog(null, "请选择将要删除的模板类别......");
				return;
			}
            int id=Integer.valueOf(tmpTypeId);
			Dao<TemplateType> dao=TemplateTypeFactory.getDaoInstance();
			int n = JOptionPane.showConfirmDialog(null, "确定删除该模板类别吗?", "确认对话框", JOptionPane.YES_NO_OPTION);
			boolean i = false;
			if (n == JOptionPane.YES_OPTION) i = dao.delete(id);
			else if (n == JOptionPane.NO_OPTION) return;
			if(i){
				JOptionPane.showMessageDialog(null, "删除模板类别成功");
				try{
					Object[][] results=getFileStates(dao.findAll());
					table.setModel(model);
					model.setDataVector(results,columnNames);
				}catch(Exception ex){ex.printStackTrace();}

			}
		}
	}
	class CloseActionListener implements ActionListener {			// 添加关闭按钮的事件监听器
		public void actionPerformed(final ActionEvent e) {
			doDefaultCloseAction();
		}
	}
}
