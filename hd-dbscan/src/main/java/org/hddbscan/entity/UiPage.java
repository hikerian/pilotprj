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
	@Column(name="page_id", length=22, nullable=false)
	private long id;
	
	@Column(name="page_nm", length=100, nullable=false)
	private String nm;
	
	@Column(name="page_desc", length=300, nullable=false)
	private String desc;


	public UiPage() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(desc, id, nm);
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
		return Objects.equals(desc, other.desc) && id == other.id && Objects.equals(nm, other.nm);
	}

	@Override
	public String toString() {
		return "UiPage [id=" + id + ", nm=" + nm + ", desc=" + desc + "]";
	}



}