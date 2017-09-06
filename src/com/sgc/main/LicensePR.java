package com.sgc.main;

import com.sgc.util.CreatecdIcon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;


/**
 * 主窗体
 * 
 */
public class LicensePR extends JFrame {
	private static final JDesktopPane 
				DESKTOP_PANE = new JDesktopPane();//桌面窗体
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());//设置系统界面外观
			new LicensePR();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public static void addIFame(JInternalFrame iframe) { // 添加子窗体的方法
		DESKTOP_PANE.add(iframe);	//新增子窗体
	}
	public LicensePR() {
		super();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);////设置关闭按钮处理事件
		Toolkit tool = Toolkit.getDefaultToolkit();				//获得默认的工具箱
		Dimension screenSize = tool.getScreenSize();			//获得屏幕的大小
		setSize(1100, 700);										//设置窗体大小
		setLocation((screenSize.width - getWidth()) / 2,
				(screenSize.height - getHeight()) / 2);			//设置窗体位置
		setTitle("汽车牌照识别系统");								//设置窗体标题
		JMenuBar menuBar = createMenu(); 	//调用创建菜单栏的方法
		setJMenuBar(menuBar);				//设置菜单栏
		JToolBar toolBar = createToolBar(); // 调用创建工具栏的方法
		getContentPane().add(toolBar, BorderLayout.NORTH);//设置工具栏
		final JLabel label = new JLabel();	//创建一个标签，用来显示图片
		label.setBounds(0, 0, 0, 0);		//设置窗体的大小和位置
		label.setIcon(null); // 窗体背景
		DESKTOP_PANE.addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				Dimension size = e.getComponent().getSize();//获得组件大小
				label.setSize(e.getComponent().getSize());//设置标签大小
				label.setText("<html><img width=" + size.width + " height="
						+ size.height + " src='"
						+ this.getClass().getResource("/mainbg.jpg")
						+ "'></html>");//设置标签文本
			}
		});
		DESKTOP_PANE.add(label,new Integer(Integer.MIN_VALUE));//将标签添加到桌面窗体
		getContentPane().add(DESKTOP_PANE);//将桌面窗体添加到主窗体中
		setVisible(true);
	}
	/**
	 * 创建工具栏
	 * 
	 * @return JToolBar
	 */
	private JToolBar createToolBar() { // 创建工具栏的方法
		JToolBar toolBar = new JToolBar();		//初始化工具栏
		toolBar.setFloatable(false);			//设置是否可以移动工具栏
		toolBar.setBorder(new BevelBorder(BevelBorder.RAISED));//设置边框
		JButton bookAddButton=new JButton(MenuActions.BOOK_ADD);//图书信息添加按钮
		JButton bookTypeAddButton=new JButton(MenuActions.BOOKTYPE_ADD);//图书类别添加按钮
		ImageIcon bookTypeAddicon=CreatecdIcon.add("bookTypeAddtb.jpg");//创建图标方法
		bookTypeAddButton.setIcon(bookTypeAddicon);//设置按钮图标
		bookTypeAddButton.setHideActionText(true);//显示提示文本
		toolBar.add(bookTypeAddButton);//添加到工具栏中
		

		
		JButton ExitButton=new JButton(MenuActions.EXIT);//退出系统按钮
		ImageIcon Exiticon=CreatecdIcon.add("exittb.jpg");//创建图标方法
		ExitButton.setIcon(Exiticon);//设置按钮图标
		ExitButton.setHideActionText(true);//显示提示文本
		toolBar.add(ExitButton);//添加到工具栏中
		return toolBar;
	}
	/**
	 * 创建菜单栏
	 */
	private JMenuBar createMenu() { // 创建菜单栏的方法
		JMenuBar menuBar = new JMenuBar();//创建工具栏
		JMenu baseMenu = new JMenu();// 初始化基础数据维护菜单
		baseMenu.setIcon(CreatecdIcon.add("jcsjcd.jpg"));//设置菜单图标
		JMenu bookTypeManageMItem = new JMenu("模板类别管理");//新增图书类别管理子菜单
		bookTypeManageMItem.add(MenuActions.BOOKTYPE_ADD);//添加图书类型添加菜单项
		bookTypeManageMItem.add(MenuActions.BOOKTYPE_MODIFY);//添加图书类型修改菜单项

		JMenu menu = new JMenu("模板库管理");//新增图书信息管理子菜单
		menu.add(MenuActions.BOOK_ADD);//添加图书信息添加菜单项
		menu.add(MenuActions.BOOK_MODIFY);//添加图书信息修改菜单项

		baseMenu.add(bookTypeManageMItem);//添加图书类别管理子菜单
		baseMenu.add(menu); // 添加模板库管理子菜单
		baseMenu.add(MenuActions.TMP_MAKE);//添加标准模板制作菜单
		baseMenu.addSeparator();		//添加分隔线
		baseMenu.add(MenuActions.EXIT);//添加退出系统菜单项

		JMenu recgMenu = new JMenu();// 初始系统功能菜单
		recgMenu.setIcon(CreatecdIcon.add("xtgncd.jpg"));//设置菜单图标

		JMenu processShowMItem = new JMenu("演示");//新增图书类别管理子菜单
		processShowMItem.add(MenuActions.SHOW_PROC);//添加图书类型添加菜单项
		recgMenu.add(processShowMItem);
		JMenu recgMItem = new JMenu("车牌识别");//新增图书类别管理子菜单
		recgMItem.add(MenuActions.TEMPLATE_RECG);
		recgMenu.add(recgMItem);

		menuBar.add(baseMenu);
		menuBar.add(recgMenu);

		return menuBar;
	}
}
