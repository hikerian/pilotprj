package org.hddbscan.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;


@Entity
@Table(name="ui_elements")
@IdClass(UiElementsId.class)
public class UiElements {
	@Id
	@Column(name="page_id", length=22, nullable=false)
	private long pageId;
	
	@Id
	@Column(name="element_id", length=22, nullable=false)
	private long id;

	@Column(name="selector", length=255, nullable=false)
	private String selector;
	
	@Column(name="classNames", length=255, nullable=false)
	private String classNames;
	
	@Column(name="text", length=255, nullable=false)
	private String text;
	
	@Column(name="left", length=10, nullable=false)
	private double left;
	
	@Column(name="top", length=10, nullable=false)
	private double top;
	
	@Column(name="width", length=10, nullable=false)
	private double width;
	
	@Column(name="height", length=10, nullable=false)
	private double height;
	
	@Column(name="major_yn", length=1, nullable=false)
	private boolean major;

	
	public UiElements() {
	}

	public UiElements(long pageId, long id, String selector, String classNames, String text, double left, double top,
			double width, double height, boolean major) {
		super();
		this.pageId = pageId;
		this.id = id;
		this.selector = selector;
		this.classNames = classNames;
		this.text = text;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.major = major;
	}

	public long getPageId() {
		return pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
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

	public boolean isMajor() {
		return major;
	}

	public void setMajor(boolean major) {
		this.major = major;
	}

	@Override
	public int hashCode() {
		return Objects.hash(classNames, height, id, left, major, pageId, selector, text, top, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UiElements other = (UiElements) obj;
		return Objects.equals(classNames, other.classNames)
				&& Double.doubleToLongBits(height) == Double.doubleToLongBits(other.height) && id == other.id
				&& Double.doubleToLongBits(left) == Double.doubleToLongBits(other.left) && major == other.major
				&& pageId == other.pageId && Objects.equals(selector, other.selector)
				&& Objects.equals(text, other.text)
				&& Double.doubleToLongBits(top) == Double.doubleToLongBits(other.top)
				&& Double.doubleToLongBits(width) == Double.doubleToLongBits(other.width);
	}

	@Override
	public String toString() {
		return "UiElements [pageId=" + pageId + ", id=" + id + ", selector=" + selector + ", classNames=" + classNames
				+ ", text=" + text + ", left=" + left + ", top=" + top + ", width=" + width + ", height=" + height
				+ ", major=" + major + "]";
	}


}
