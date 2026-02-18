package org.hddbscan.entity;

import java.util.Objects;


public class UiElementsId {
	private long pageId;
	private long elementId;
	
	
	public UiElementsId() {
	}

	public UiElementsId(long pageId, long elementId) {
		super();
		this.pageId = pageId;
		this.elementId = elementId;
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

	@Override
	public int hashCode() {
		return Objects.hash(elementId, pageId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UiElementsId other = (UiElementsId) obj;
		return elementId == other.elementId && pageId == other.pageId;
	}

	@Override
	public String toString() {
		return "UiElementsId [pageId=" + pageId + ", elementId=" + elementId + "]";
	}


}
