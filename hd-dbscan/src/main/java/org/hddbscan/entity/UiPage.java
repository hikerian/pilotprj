package org.hddbscan.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="ui_page")
public class UiPage {
	@Id
	@Column(name="page_id", columnDefinition="DECIMAL(5)", nullable=false)
	private long pageId;
	
	@Column(name="page_nm", columnDefinition="VARCHAR(200)", nullable=false)
	private String pageNm;
	
	@Column(name="page_desc", columnDefinition="VARCHAR(300)", nullable=false)
	private String pageDesc;


	public UiPage() {
	}

	public UiPage(long pageId, String pageNm, String pageDesc) {
		super();
		this.pageId = pageId;
		this.pageNm = pageNm;
		this.pageDesc = pageDesc;
	}

	public long getPageId() {
		return pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public String getPageNm() {
		return pageNm;
	}

	public void setPageNm(String pageNm) {
		this.pageNm = pageNm;
	}

	public String getPageDesc() {
		return pageDesc;
	}

	public void setPageDesc(String pageDesc) {
		this.pageDesc = pageDesc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pageDesc, pageId, pageNm);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UiPage other = (UiPage) obj;
		return Objects.equals(pageDesc, other.pageDesc) && pageId == other.pageId
				&& Objects.equals(pageNm, other.pageNm);
	}

	@Override
	public String toString() {
		return "UiPage [pageId=" + pageId + ", pageNm=" + pageNm + ", pageDesc=" + pageDesc + "]";
	}


}