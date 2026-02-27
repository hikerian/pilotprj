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
	// FIXME: 규칙 기반으로 컴포넌트를 분류할 수 있는 기능이 필요함
	private final static List<String> BUTTON_CLASSES = Arrays.asList("btn-header-minus", "cl-button", "btn-search", "btn-restore", "btn-save", "btn-excel"
			, "btn-setting", "btn-pop", "btn-new", "btn-delete", "btn-pop-search");

	private final static List<String> INPUT_CLASSES = Arrays.asList("cl-inputbox", "cl-combobox", "cl-numbereditor", "cl-checkbox", "cl-dateinput");

	private final static List<String> OUTPUT_CLASSES = Arrays.asList("cl-output", "cl-default-cell", "table-row-cnt");
	
	private final static List<String> GROUPTITLE_CLASSES = Arrays.asList("grp-title", "grd-title");
	
	
	public static String genId(UiElements uiElements) {
		return String.format("%1$d-%2$d", uiElements.getPageId(), uiElements.getElementId());
	}
	
	public static UIElementDataRow convert(UiElements uiElements) {
		String id = UIElementDataRow.genId(uiElements);
		String selector = uiElements.getSelectorText();
		String[] classNames = uiElements.getClassNames().split(",");
		double left = uiElements.getPosLeft();
		double top = uiElements.getPosTop();
		double width = uiElements.getUiWidth();
		double height = uiElements.getUiHeight();
		
		return UIElementDataRow.convert(id, selector, classNames, left, top, width, height);
	}
	
	public static UIElementDataRow convert(String id, JSONObject target) {
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		String selector = target.getAsString("selector");
		String[] classNames = classNameArray.toArray(new String[0]);
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		double left = clientRect.getAsNumber("left").doubleValue();
		double top = clientRect.getAsNumber("top").doubleValue();
		double width = clientRect.getAsNumber("width").doubleValue();
		double height = clientRect.getAsNumber("height").doubleValue();
		
		return UIElementDataRow.convert(id, selector, classNames, left, top, width, height);
	}
	
	private static UIElementDataRow convert(String id, String selector, String[] classNms, double left, double top, double width, double height) {
		if(width == 0D || height == 0D) {
			// 크기가 0이면 skip!
			return null;
		}
		
		UIElementDataRow dataRow = new UIElementDataRow();
		dataRow.setId(id);
		
		dataRow.setSearchBox(selector.contains("search-box"));
		dataRow.setDataBox(selector.contains("data-box"));
		dataRow.setFormBox(selector.contains("form-box"));
		
		dataRow.setGrpTitle(UIElementDataRow.isContains(UIElementDataRow.GROUPTITLE_CLASSES, selector));
		
		dataRow.setGridHeader(selector.contains("cl-grid-header"));
		dataRow.setGridDetail(selector.contains("cl-grid-detail"));
		
		dataRow.setTabfolderHeader(selector.contains("cl-tabfolder-header"));
		
		dataRow.setDataTitleEl(UIElementDataRow.isContains("data-title", classNms));
		
		dataRow.setButtonEl(UIElementDataRow.isContains(UIElementDataRow.BUTTON_CLASSES, classNms));
		dataRow.setInputEl(UIElementDataRow.isContains(UIElementDataRow.INPUT_CLASSES, classNms));
		dataRow.setOutputEl(UIElementDataRow.isContains(UIElementDataRow.OUTPUT_CLASSES, classNms));
		dataRow.setPosition(left, top, width, height);
		
		return dataRow;		
	}
	
	private static boolean isContains(String cond, String[] items) {
		for(String item : items) {
			if(cond.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isContains(List<String> container, String[] items) {
		for(String item : items) {
			if(container.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isContains(List<String> container, String selector) {
		for(String item : container) {
			if(selector.contains(item)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private String id;
	
	private boolean searchBox; // search-box
	private boolean dataBox; // data-box
	private boolean formBox; // form-box
	
	private boolean grpTitle; // grd-title OR grp-title
	
	private boolean gridHeader; // cl-grid-header
	private boolean gridDetail; // cl-grid-detail
	
	private boolean tabfolderHeader; // cl-tabfolder-header
	
	private boolean dataTitleEl; // data-title
	private boolean buttonEl;
	private boolean inputEl;
	private boolean outputEl;
	private double left;
	private double top;
	private double width;
	private double height;
	
	
	public UIElementDataRow() {
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setSearchBox(boolean searchBox) {
		this.searchBox = searchBox;
	}

	public void setDataBox(boolean dataBox) {
		this.dataBox = dataBox;
	}

	public void setFormBox(boolean formBox) {
		this.formBox = formBox;
	}
	
	public void setGrpTitle(boolean grpTitle) {
		this.grpTitle = grpTitle;
	}

	public void setGridHeader(boolean gridHeader) {
		this.gridHeader = gridHeader;
	}
	
	public void setGridDetail(boolean gridDetail) {
		this.gridDetail = gridDetail;
	}

	public void setTabfolderHeader(boolean tabfolderHeader) {
		this.tabfolderHeader = tabfolderHeader;
	}

	public void setDataTitleEl(boolean dataTitleEl) {
		this.dataTitleEl = dataTitleEl;
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

	public void setPosition(double left, double top, double width, double height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public DataRow toDataRow() {
		DataRow row = new DataRow();
		row.setId(this.id);
		
		row.setData(new DoubleFeature(this.searchBox ? 1D : 0D)
				, new DoubleFeature(this.dataBox ? 1D : 0D)
				, new DoubleFeature(this.formBox ? 1D : 0D)
				
				, new DoubleFeature(this.grpTitle ? 1D : 0D)
				
				, new DoubleFeature(this.gridHeader ? 1D : 0D)
				, new DoubleFeature(this.gridDetail ? 1D : 0D)
				, new DoubleFeature(this.tabfolderHeader ? 1D : 0D)
				
				, new DoubleFeature(this.dataTitleEl ? 1D : 0D)
				
				, new DoubleFeature(this.buttonEl ? 1D : 0D)
				, new DoubleFeature(this.inputEl ? 1D : 0D)
				, new DoubleFeature(this.outputEl ? 1D : 0D)
				, new PositionFeature(this.left, this.top, this.width, this.height));
		
		return row;
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append(String.join(delimiter
				, this.id
				, String.valueOf(this.searchBox)
				, String.valueOf(this.dataBox)
				, String.valueOf(this.formBox)
				
				, String.valueOf(this.grpTitle)
				
				, String.valueOf(this.gridHeader)
				, String.valueOf(this.gridDetail)
				, String.valueOf(this.tabfolderHeader)
				
				, String.valueOf(this.dataTitleEl)
				
				, String.valueOf(this.buttonEl)
				, String.valueOf(this.inputEl)
				, String.valueOf(this.outputEl)
				, String.valueOf(this.left)
				, String.valueOf(this.top)
				, String.valueOf(this.width)
				, String.valueOf(this.height)
				)).append('\n');
	}

	@Override
	public String toString() {
		return "UIElementDataRow [id=" + id + ", searchBox=" + searchBox + ", dataBox=" + dataBox + ", formBox="
				+ formBox + ", grpTitle=" + grpTitle + ", gridHeader=" + gridHeader + ", gridDetail=" + gridDetail
				+ ", tabfolderHeader=" + tabfolderHeader + ", dataTitleEl=" + dataTitleEl + ", buttonEl=" + buttonEl
				+ ", inputEl=" + inputEl + ", outputEl=" + outputEl + ", left=" + left + ", top=" + top + ", width="
				+ width + ", height=" + height + "]";
	}





}
