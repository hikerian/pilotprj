package org.hddbscan.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hddbscan.dbscan.DataRow;
import org.hddbscan.dbscan.feature.DoubleFeature;
import org.hddbscan.dbscan.feature.PositionFeature;
import org.hddbscan.entity.UiElements;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


public class UIElementDataRow {
	private final static List<String> BUTTON_CLASSES = Arrays.asList("btn-header-minus", "cl-button", "btn-search", "btn-restore", "btn-save", "btn-excel"
			, "btn-setting", "btn-pop", "btn-new", "btn-delete", "btn-pop-search");
	private final static List<String> INPUT_CLASSES = Arrays.asList("cl-inputbox", "cl-combobox", "cl-numbereditor", "cl-checkbox", "cl-dateinput");
	private final static List<String> OUTPUT_CLASSES = Arrays.asList("cl-output", "data-title", "table-row-cnt");
	
	
	public static String genId(UiElements uiElements) {
		return String.format("%1$d-%2$d", uiElements.getPageId(), uiElements.getElementId());
	}
	
	public static UIElementDataRow convert(UiElements uiElements) {
		String id = UIElementDataRow.genId(uiElements);
		
		String[] classNames = uiElements.getClassNames().split(",");
		double left = uiElements.getPosLeft();
		double top = uiElements.getPosTop();
		double width = uiElements.getUiWidth();
		double height = uiElements.getUiHeight();
		
		if(width == 0D || height == 0D) {
			// 크기가 0이면 skip!
			return null;
		}
		
		UIElementDataRow dataRow = new UIElementDataRow();
		dataRow.setId(id);
		dataRow.setButtonEl(UIElementDataRow.isContains(UIElementDataRow.BUTTON_CLASSES, classNames));
		dataRow.setInputEl(UIElementDataRow.isContains(UIElementDataRow.INPUT_CLASSES, classNames));
		dataRow.setOutputEl(UIElementDataRow.isContains(UIElementDataRow.OUTPUT_CLASSES, classNames));
		dataRow.setLeft(left);
		dataRow.setTop(top);
		
		return dataRow;
	}
	
	public static UIElementDataRow convert(String id, JSONObject target) {
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		String[] classNms = classNameArray.toArray(new String[0]);
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		double left = clientRect.getAsNumber("left").doubleValue();
		double top = clientRect.getAsNumber("top").doubleValue();
		double width = clientRect.getAsNumber("width").doubleValue();
		double height = clientRect.getAsNumber("height").doubleValue();
		
		if(width == 0D || height == 0D) {
			// 크기가 0이면 skip!
			return null;
		}
		
		UIElementDataRow dataRow = new UIElementDataRow();
		dataRow.setId(id);
		dataRow.setButtonEl(UIElementDataRow.isContains(UIElementDataRow.BUTTON_CLASSES, classNms));
		dataRow.setInputEl(UIElementDataRow.isContains(UIElementDataRow.INPUT_CLASSES, classNms));
		dataRow.setOutputEl(UIElementDataRow.isContains(UIElementDataRow.OUTPUT_CLASSES, classNms));
		dataRow.setLeft(left);
		dataRow.setTop(top);
		
		return dataRow;
	}
	
	private static boolean isContains(List<String> container, String[] items) {
		for(String item : items) {
			if(container.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private String id;
	private boolean buttonEl;
	private boolean inputEl;
	private boolean outputEl;
	private double left; // TODO left와 top은 좌표계 포지션으로 각각 계산하지 말고 거리로 계산하도록 변경
	private double top;  // TODO left와 top은 좌표계 포지션으로 각각 계산하지 말고 거리로 계산하도록 변경
	
	
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

	public DataRow toDataRow() {
		DataRow row = new DataRow();
		row.setId(this.id);
		row.setData(new DoubleFeature(this.buttonEl ? 1D : 0D)
				, new DoubleFeature(this.inputEl ? 1D : 0D)
				, new DoubleFeature(this.outputEl ? 1D : 0D)
				, new PositionFeature(this.left, this.top));
		
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
				+ "]";
	}
	

}
