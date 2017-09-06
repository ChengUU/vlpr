package com.sgc.util;

import com.sgc.main.LicensePR;

import java.net.URL;

import javax.swing.ImageIcon;


public class CreatecdIcon {
	public static ImageIcon add(String ImageName){
		URL IconUrl = LicensePR.class.getResource("/"+ImageName);
		ImageIcon icon=new ImageIcon(IconUrl);
		return icon;
	}
}
