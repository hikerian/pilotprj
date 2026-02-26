package org.hddbscan.controller;

import java.util.List;
import java.util.Objects;

import org.hddbscan.entity.UiElements;


public class ModelGroup {
	private String id;
	private String rangeText;
	private List<UiElements> uiElementList;
	
	public ModelGroup() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRangeText() {
		return rangeText;
	}

	public void setRangeText(String rangeText) {
		this.rangeText = rangeText;
	}

	public List<UiElements> getUiElementList() {
		return uiElementList;
	}

	public void setUiElementList(List<UiElements> uiElementList) {
		this.uiElementList = uiElementList;
	}
	
	public int getUiElementListSize() {
		return this.uiElementList.size();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, rangeText, uiElementList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelGroup other = (ModelGroup) obj;
		return Objects.equals(id, other.id) && Objects.equals(rangeText, other.rangeText)
				&& Objects.equals(uiElementList, other.uiElementList);
	}

	@Override
	public String toString() {
		return "ModelGroup [id=" + id + ", rangeText=" + rangeText + ", uiElementList=" + uiElementList + "]";
	}


}
