package org.hddbscan.preprocessing;

import java.util.Objects;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


/**
 * TODO: Entity를 활용하여 전처리를 수행하도록 수정
 */
public class UIElementRaw {
	private String id;
	private String classNames;
	private String selector;
	private String text;
	private double left;
	private double top;
	private double width;
	private double height;
	
	
	public UIElementRaw() {
	}
	
	public UIElementRaw(String id, JSONObject target) {
		this.id = id;
		
		JSONArray classNameArray = (JSONArray) target.get("classNames");
		this.classNames = String.join(",", classNameArray.toArray(new String[0]));
		
		this.selector = target.getAsString("selector");
		
		JSONObject clientRect = (JSONObject)target.get("clientRect");
		this.left = clientRect.getAsNumber("left").doubleValue();
		this.top = clientRect.getAsNumber("top").doubleValue();
		this.width = clientRect.getAsNumber("width").doubleValue();
		this.height = clientRect.getAsNumber("height").doubleValue();
	}

	public UIElementRaw(String id, String classNames, String selector, String text, double left, double top, double width,
			double height) {
		super();
		this.id = id;
		this.classNames = classNames;
		this.selector = selector;
		this.text = text;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getLeft() {
		return left;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public double getTop() {
		return top;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(classNames, height, id, left, selector, text, top, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UIElementRaw other = (UIElementRaw) obj;
		return Objects.equals(classNames, other.classNames)
				&& Double.doubleToLongBits(height) == Double.doubleToLongBits(other.height)
				&& Objects.equals(id, other.id) && Double.doubleToLongBits(left) == Double.doubleToLongBits(other.left)
				&& Objects.equals(selector, other.selector) && Objects.equals(text, other.text)
				&& Double.doubleToLongBits(top) == Double.doubleToLongBits(other.top)
				&& Double.doubleToLongBits(width) == Double.doubleToLongBits(other.width);
	}

	@Override
	public String toString() {
		return "UIElement [id=" + id + ", classNames=" + classNames + ", selector=" + selector + ", text=" + text
				+ ", left=" + left + ", top=" + top + ", width=" + width + ", height=" + height + "]";
	}


}
