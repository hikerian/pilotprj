package org.hddbscan.entity;

import java.util.Objects;


public class UiElementsId {
	private long pageId;
	private long id;
	
	
	public UiElementsId() {
	}

	public UiElementsId(long pageId, long id) {
		super();
		this.pageId = pageId;
		this.id = id;
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

	@Override
	public int hashCode() {
		return Objects.hash(id, pageId);
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
		return id == other.id && pageId == other.pageId;
	}

	@Override
	public String toString() {
		return "UiElementsId [pageId=" + pageId + ", id=" + id + "]";
	}


}
