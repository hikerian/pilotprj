package org.hddbscan.controller;

import java.util.Objects;


public class ModelDesc {
	private String id;
	private String label;
	private String rangeText;
	private int elementCnt;
	
	public ModelDesc() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getRangeText() {
		return rangeText;
	}

	public void setRangeText(String rangeText) {
		this.rangeText = rangeText;
	}

	public int getElementCnt() {
		return elementCnt;
	}

	public void setElementCnt(int elementCnt) {
		this.elementCnt = elementCnt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementCnt, id, label, rangeText);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelDesc other = (ModelDesc) obj;
		return elementCnt == other.elementCnt && Objects.equals(id, other.id) && Objects.equals(label, other.label)
				&& Objects.equals(rangeText, other.rangeText);
	}

	@Override
	public String toString() {
		return "ModelDesc [id=" + id + ", label=" + label + ", rangeText=" + rangeText + ", elementCnt=" + elementCnt
				+ "]";
	}


}
