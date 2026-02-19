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
	@Column(name="page_id", columnDefinition="DECIMAL(5)", nullable=false)
	private long pageId;
	
	@Id
	@Column(name="element_id", columnDefinition="DECIMAL(5)", nullable=false)
	private long elementId;

	@Column(name="selector_text", columnDefinition="VARCHAR(2000)", nullable=false)
	private String selectorText;
	
	@Column(name="class_names", columnDefinition="VARCHAR(1000)", nullable=false)
	private String classNames;
	
	@Column(name="ctnt_text", columnDefinition="VARCHAR(1000)", nullable=false)
	private String ctntText;
	
	@Column(name="pos_left", columnDefinition="DECIMAL(10, 2)", nullable=false)
	private double posLeft;
	
	@Column(name="pos_top", columnDefinition="DECIMAL(10, 2)", nullable=false)
	private double posTop;
	
	@Column(name="ui_width", columnDefinition="DECIMAL(10, 2)", nullable=false)
	private double uiWidth;
	
	@Column(name="ui_height", columnDefinition="DECIMAL(10, 2)", nullable=false)
	private double uiHeight;
	
	@Column(name="major_yn", columnDefinition="VARCHAR(1)", nullable=false)
	private String majorYn;

	
	public UiElements() {
	}

	public UiElements(long pageId, long elementId, String selectorText, String classNames, String ctntText,
			double posLeft, double posTop, double uiWidth, double uiHeight, String majorYn) {
		super();
		this.pageId = pageId;
		this.elementId = elementId;
		this.selectorText = selectorText;
		this.classNames = classNames;
		this.ctntText = ctntText;
		this.posLeft = posLeft;
		this.posTop = posTop;
		this.uiWidth = uiWidth;
		this.uiHeight = uiHeight;
		this.majorYn = majorYn;
	}

	public long getPageId() {
		return pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public String getSelectorText() {
		return selectorText;
	}

	public void setSelectorText(String selectorText) {
		this.selectorText = selectorText;
	}

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}

	public String getCtntText() {
		return ctntText;
	}

	public void setCtntText(String ctntText) {
		this.ctntText = ctntText;
	}

	public double getPosLeft() {
		return posLeft;
	}

	public void setPosLeft(double posLeft) {
		this.posLeft = posLeft;
	}

	public double getPosTop() {
		return posTop;
	}

	public void setPosTop(double posTop) {
		this.posTop = posTop;
	}

	public double getUiWidth() {
		return uiWidth;
	}

	public void setUiWidth(double uiWidth) {
		this.uiWidth = uiWidth;
	}

	public double getUiHeight() {
		return uiHeight;
	}

	public void setUiHeight(double uiHeight) {
		this.uiHeight = uiHeight;
	}

	public String getMajorYn() {
		return majorYn;
	}

	public void setMajorYn(String majorYn) {
		this.majorYn = majorYn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(classNames, ctntText, elementId, majorYn, pageId, posLeft, posTop, selectorText, uiHeight,
				uiWidth);
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
		return Objects.equals(classNames, other.classNames) && Objects.equals(ctntText, other.ctntText)
				&& elementId == other.elementId && Objects.equals(majorYn, other.majorYn) && pageId == other.pageId
				&& Double.doubleToLongBits(posLeft) == Double.doubleToLongBits(other.posLeft)
				&& Double.doubleToLongBits(posTop) == Double.doubleToLongBits(other.posTop)
				&& Objects.equals(selectorText, other.selectorText)
				&& Double.doubleToLongBits(uiHeight) == Double.doubleToLongBits(other.uiHeight)
				&& Double.doubleToLongBits(uiWidth) == Double.doubleToLongBits(other.uiWidth);
	}

	@Override
	public String toString() {
		return "UiElements [pageId=" + pageId + ", elementId=" + elementId + ", selectorText=" + selectorText
				+ ", classNames=" + classNames + ", ctntText=" + ctntText + ", posLeft=" + posLeft + ", posTop="
				+ posTop + ", uiWidth=" + uiWidth + ", uiHeight=" + uiHeight + ", majorYn=" + majorYn + "]";
	}



}
