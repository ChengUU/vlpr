package com.sgc.main;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JInternalFrame;

import com.sgc.iframe.*;

/**
 * 菜单和按钮的Action对象
 * 
 */
public class MenuActions {
	private static Map<String, JInternalFrame> frames; // 子窗体集合


	public static BookTypeModiAction BOOKTYPE_MODIFY; 	// 模板类别修改窗体动作
	public static BookTypeAddAction BOOKTYPE_ADD; 		// 模板类别添加窗体动作
	public static BookModiAction BOOK_MODIFY; 			// 图书信息修改窗体动作
	public static BookAddAction BOOK_ADD; 				// 图书信息添加窗体动作
	public static TmpMakeAction TMP_MAKE; 				// 标准模板制作动作

	public static ShowProcAction SHOW_PROC; 			//流程演示
    public static TmpRecgAction TEMPLATE_RECG; 			// 模板匹配识别

	public static ExitAction EXIT; 						// 系统退出动作

	static {
		frames = new HashMap<String, JInternalFrame>();

		BOOKTYPE_MODIFY = new BookTypeModiAction();
		BOOKTYPE_ADD = new BookTypeAddAction();
		BOOK_MODIFY = new BookModiAction();
		BOOK_ADD = new BookAddAction();
		TMP_MAKE=new TmpMakeAction();
        SHOW_PROC=new ShowProcAction();
        TEMPLATE_RECG=new TmpRecgAction();
		EXIT = new ExitAction();
	}
	private static class TmpMakeAction extends AbstractAction {
		TmpMakeAction() {
			super("标准模板制作", null);
			putValue(Action.LONG_DESCRIPTION, "标准模板制作");
			putValue(Action.SHORT_DESCRIPTION, "制作标准模板");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("标准模板制作")||frames.get("标准模板制作").isClosed()) {
				TmpMakeFrame iframe=new TmpMakeFrame();
				frames.put("标准模板制作", iframe);
				LicensePR.addIFame(frames.get("标准模板制作"));
			}
		}
	}

	private static class TmpRecgAction extends AbstractAction {
        TmpRecgAction() {
			super("模板匹配识别", null);
			putValue(Action.LONG_DESCRIPTION, "模板匹配识别字符");
			putValue(Action.SHORT_DESCRIPTION, "模板匹配识别");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("模板匹配识别")||frames.get("模板匹配识别").isClosed()) {
                TemplateRecgFrame iframe=new TemplateRecgFrame();
				frames.put("模板匹配识别", iframe);
				LicensePR.addIFame(frames.get("模板匹配识别"));
			}
		}
	}
    private static class ShowProcAction extends AbstractAction {
        ShowProcAction() {
            super("流程演示", null);
            putValue(Action.LONG_DESCRIPTION, "系统工作流程演示");
            putValue(Action.SHORT_DESCRIPTION, "工作流程演示");
        }
        public void actionPerformed(ActionEvent e) {
            if (!frames.containsKey("工作流程演示")||frames.get("工作流程演示").isClosed()) {
                MainUI iframe=new MainUI();
                frames.put("工作流程演示", iframe);
                LicensePR.addIFame(frames.get("工作流程演示"));
            }
        }
    }



	private static class BookTypeModiAction extends AbstractAction {
		BookTypeModiAction() {
			super("模板类别信息修改", null);
			putValue(Action.LONG_DESCRIPTION, "修改模板类别信息");
			putValue(Action.SHORT_DESCRIPTION, "模板类别信息修改");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("模板类别修改")||frames.get("模板类别修改").isClosed()) {
				BookTypeModiAndDelIFrame iframe=new BookTypeModiAndDelIFrame();
				frames.put("模板类别修改", iframe);
				LicensePR.addIFame(frames.get("模板类别修改"));
			}
		}
	}

	private static class BookTypeAddAction extends AbstractAction {
		BookTypeAddAction() {
			super("模板类别添加", null);
			putValue(Action.LONG_DESCRIPTION, "为模板库添加新的模板类别");
			putValue(Action.SHORT_DESCRIPTION, "模板类别添加");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("模板类别添加")||frames.get("模板类别添加").isClosed()) {
				BookTypeAddIFrame iframe=new BookTypeAddIFrame();
				frames.put("模板类别添加", iframe);
				LicensePR.addIFame(frames.get("模板类别添加"));
			}
		}
	}
	//图书修改与删除
	private static class BookModiAction extends AbstractAction {
		BookModiAction() {
			super("匹配模板修改", null);
			putValue(Action.LONG_DESCRIPTION, "修改和删除模板信息");
			putValue(Action.SHORT_DESCRIPTION, "匹配模板修改");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("匹配模板修改")||frames.get("匹配模板修改").isClosed()) {
				BookModiAndDelIFrame iframe=new BookModiAndDelIFrame();
				frames.put("匹配模板修改", iframe);
				LicensePR.addIFame(frames.get("匹配模板修改"));
			}
		}
	}
	private static class BookAddAction extends AbstractAction {				// 图书信息添加－－－已经实现，请参照
		BookAddAction() {

			super("添加识别模板", null);
			//super();
			putValue(Action.LONG_DESCRIPTION, "为模板库添加新的模板");
			putValue(Action.SHORT_DESCRIPTION, "添加识别模板");
		}
		public void actionPerformed(ActionEvent e) {
			if (!frames.containsKey("添加识别模板")||frames.get("添加识别模板").isClosed()) {
				BookAddIFrame iframe = new BookAddIFrame();
				frames.put("添加识别模板", iframe);
				LicensePR.addIFame(frames.get("添加识别模板"));
			}
		}
	}
	private static class ExitAction extends AbstractAction { // 退出系统动作
		public ExitAction() {
			super("退出系统", null);
			putValue(Action.LONG_DESCRIPTION, "退出车牌识别系统");
			putValue(Action.SHORT_DESCRIPTION, "退出系统");
		}
		public void actionPerformed(final ActionEvent e) {
			System.exit(0);
		}
	}

	private MenuActions() {
		super();
	}

}
