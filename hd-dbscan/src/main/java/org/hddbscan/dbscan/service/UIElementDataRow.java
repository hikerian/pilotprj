package org.hddbscan.dbscan.service;

import java.io.IOException;
import java.util.Arrays;

import org.hddbscan.dbscan.DataRow;


public class UIElementDataRow {
	private String id;
	private boolean buttonEl;
	private boolean inputEl;
	private boolean outputEl;
	private double left;
	private double top;
	
//	private double width;
//	private double height;
//	private String selector;
//	private String text; /* 없는 데이터가 너무 많음 */
//	private String[] classNames;
//	private boolean major;
	
	
	public UIElementDataRow() {
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setButtonEl(boolean buttonEl) {
		this.buttonEl = buttonEl;
	}

	public void setInputEl(boolean inputEl) {
		this.inputEl = inputEl;
	}

	public void setOutputEl(boolean outputEl) {
		this.outputEl = outputEl;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public void setTop(double top) {
		this.top = top;
	}

//	public void setWidth(double width) {
//		this.width = width;
//	}
//
//	public void setHeight(double height) {
//		this.height = height;
//	}
//
//	public void setSelector(String selector) {
//		this.selector = selector;
//	}
//
//	public void setText(String text) {
//		this.text = text;
//	}
//
//	public void setClassNames(String[] classNames) {
//		this.classNames = classNames;
//	}
//
//	public void setMajor(boolean major) {
//		this.major = major;
//	}

	public DataRow toDataRow() {
		DataRow row = new DataRow();
		row.setId(this.id);
		row.setData(this.buttonEl ? 1 : 0
				, this.inputEl ? 1 : 0
				, this.outputEl ? 1 : 0
				, this.left
				, this.top);
		
//		row.setAttrs(this.width
//				, this.height
//				, this.selector
//				, this.text
//				, this.classNames
//				, this.major);
		
		return row;
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append(String.join(delimiter
				, this.id
				, String.valueOf(this.buttonEl)
				, String.valueOf(this.inputEl)
				, String.valueOf(this.outputEl)
				, String.valueOf(this.left)
				, String.valueOf(this.top)
				)).append('\n');
	}

	@Override
	public String toString() {
		return "UIElementDataRow [id=" + id + ", buttonEl=" + buttonEl + ", inputEl=" + inputEl + ", outputEl="
				+ outputEl + ", left=" + left + ", top=" + top
//				+ ", width=" + width + ", height=" + height
//				+ ", selector=" + selector + ", text=" + text + ", classNames=" + Arrays.toString(classNames)
//				+ ", major=" + major
				+ "]";
	}
	

}
